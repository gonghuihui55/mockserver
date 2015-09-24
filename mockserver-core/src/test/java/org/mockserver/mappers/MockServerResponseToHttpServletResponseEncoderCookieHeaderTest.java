package org.mockserver.mappers;

import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jamesdbloom
 */
public class MockServerResponseToHttpServletResponseEncoderCookieHeaderTest {

    @Test
    public void shouldOnlyMapACookieIfThereIsNoSetCookieHeader() throws UnsupportedEncodingException {
        // given
        // - an HttpResponse
        HttpResponse httpResponse = new HttpResponse();
        String cookieOne = "cookieName1=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/";
        String cookieTwo = "cookieName2=\"cookie==Value2\"; Version=1; Comment=\"Anonymous cookie for site\"; Max-Age=15552000; Expires=Sat, 19-Mar-2016 18:43:26 GMT; Path=/";
        httpResponse.withHeaders(
                new Header("Set-Cookie", cookieOne),
                new Header("Set-Cookie", cookieTwo)
        );
        httpResponse.withCookies(
                new Cookie("cookieName1", ""),
                new Cookie("cookieName2", "cookie==Value2"),
                new Cookie("cookieName3", "cookie==Value3")
        );
        // - an HttpServletResponse
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);

        // then
        assertEquals(HttpStatusCode.OK_200.code(), httpServletResponse.getStatus());
        assertThat(httpServletResponse.getHeaders("Set-Cookie"), containsInAnyOrder(
                cookieOne,
                cookieTwo,
                "cookieName3=cookie==Value3"
        ));
        assertThat(httpServletResponse.getHeaderNames(), contains("Set-Cookie"));
    }
}
