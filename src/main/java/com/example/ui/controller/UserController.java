package com.example.ui.controller;

import com.example.ui.client.UserClient;
import com.example.ui.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    // 회원가입 폼
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "user/user-register";
    }

    @GetMapping("/user-register-success")
    public String showRegisterSuccessPage() {
        return "user/user-register-success"; // templates/user/user-register-success.html
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);

        try {
            userClient.createUser(newUser); // FeignClient 사용
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다! 환영합니다 😊");
            return "redirect:/user/user-register-success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "회원가입 중 오류가 발생했습니다.");
            return "redirect:/user/register";
        }
    }

    // 로그인 폼
    @GetMapping("/login")
    public String showLoginForm() {
        return "user/user-login";
    }

    // 로그인 처리 예시 (세션에 로그인 정보 저장)
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        try {
            User user = userClient.getUserByEmail(email);
            if (user.getPassword().equals(password)) {
                session.setAttribute("loggedInUserEmail", user.getEmail());
                session.setAttribute("loggedInUserName", user.getName());
                return "redirect:/book";
            } else {
                model.addAttribute("loginError", "이메일 또는 비밀번호가 틀렸습니다.");
                return "user/user-login";
            }
        } catch (feign.FeignException.NotFound e) {
            model.addAttribute("loginError", "존재하지 않는 이메일입니다.");
            return "user/user-login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("loginError", "로그인 중 오류가 발생했습니다.");
            return "user/user-login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 초기화
        return "redirect:/"; // 로그인 페이지로 리디렉션
    }

    // 내 정보 조회
    @GetMapping("/profile")
    public String viewProfile(jakarta.servlet.http.HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) {
            return "redirect:/login"; // 로그인 안 됨
        }

        User user = userClient.getUserByEmail(email); // UserClient 사용해서 조회
        model.addAttribute("user", user);
        return "user/user-profile";
    }

    // 내 정보 수정
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User updatedUser, HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) {
            return "redirect:/login";
        }

        // 현재 로그인 사용자 정보 조회 (id 필요)
        User currentUser = userClient.getUserByEmail(email);
        if (currentUser == null) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
            return "user/user-profile";
        }

        // UserClient를 통해 수정 호출
        userClient.updateUser(currentUser.getId(), updatedUser);

        // 세션에 저장된 이름 업데이트
        session.setAttribute("loggedInUserName", updatedUser.getName());

        model.addAttribute("user", updatedUser);
        model.addAttribute("success", "정보가 성공적으로 업데이트되었습니다.");

        return "user/user-profile";
    }
}
