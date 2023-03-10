package com.example.studymember.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private AccountRepository repository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        SignUpForm signUpForm = (SignUpForm)object;
        if (repository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일 입니다.");
        }

        if(repository.existsByNickname(signUpForm.getNickname())){
            errors.rejectValue("nickname", "invalid nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임 입니다");
        }
    }
}

