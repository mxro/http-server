package de.mxro.httpserver.tests;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import de.mxro.httpserver.internal.services.ResourceService_DateUtil;

public class TestIfModifiedSince {

    @Test
    public void test() {
        ResourceService_DateUtil.parseDateFromHttpHeader("1412551431938");

        final Date modifiedSince = ResourceService_DateUtil.parseDateFromHttpHeader("Thu, 29 Mar 2012 18:19:50 GMT");

        final Date lastModified = new Date();

        Assert.assertTrue(ResourceService_DateUtil.hasChanged(modifiedSince.getTime(), lastModified.getTime()));

    }

}
