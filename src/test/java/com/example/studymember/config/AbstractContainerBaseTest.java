package com.example.studymember.config;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerBaseTest {

    static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer().withDatabaseName("studytest");
        POSTGRE_SQL_CONTAINER.start();
    }

}