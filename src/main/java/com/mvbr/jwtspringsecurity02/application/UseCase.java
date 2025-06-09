package com.mvbr.jwtspringsecurity02.application;

public interface UseCase<I, O> {
    O execute(I input);
}

