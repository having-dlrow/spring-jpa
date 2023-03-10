package com.example.studymember.controller;

import com.example.studymember.account.AccountRepository;
import com.example.studymember.domain.Account;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    public void 인증토큰오류() throws Exception {

        mockMvc.perform(get("/check-email-token")
                        .param("token", "v_wrong_token")
                        .param("email", "v_wrong_email"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    @Transactional
    public void 인증토큰정상() throws Exception {

        Account account = Account.builder()
                .email("email@email.com")
                .password("12345678")
                .nickname("nickname")
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", newAccount.getEmailCheckToken())
                        .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated())
        ;
    }

    @DisplayName("회원 가입 화면")
    @Test
    public void 회원가입페이지() throws Exception {

        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("회원 가입 페이지 변수")
    @Test
    public void 회원가입페이지_변수확인() throws Exception {

        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("회원가입처리 - 입력값 오류")
    @Test
    public void 회원가입처리오류() throws Exception {
        String expected = "v_email";
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "v_nickname")
                        .param("email", expected)
                        .param("password", "v_password")
                        .with(csrf())
                )
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("회원가입처리 - 입력값 정상")
    @Test
    public void 회원가입처리정상() throws Exception {
        String expectedEmail = "v_email@email.com";
        String expectedPassword = "v_password";
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "v_nickname")
                        .param("email", expectedEmail)
                        .param("password", expectedPassword)
                        .with(csrf())
                )
//                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("v_nickname"))
        ;

        Account account = accountRepository.findByEmail(expectedEmail);
        assertNotNull(account);
        assertNotEquals(account.getPassword(), expectedPassword);
        assertNotNull(account.getEmailCheckToken()); // @transactional

        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}