package de.mxro.httpserver.tests;

import org.junit.Test;

import de.mxro.httpserver.internal.services.ResourceService_DateUtil;

public class TestIfModifiedSince {

    @Test
    public void test() {
        System.out.println(ResourceService_DateUtil.parseDateFromHttpHeader("1413231193463"));
    }

}
