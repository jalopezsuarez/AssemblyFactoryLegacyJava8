/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.web;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

/**
 *
 * @author Administrator
 */
public abstract class WebAuthenticator extends BasicAuthenticator
{
    public WebAuthenticator(String realm)
    {
        super(realm);
    }

    @Override
    public Authenticator.Result authenticate(HttpExchange exchange)
    {
        Authenticator.Result authenticate = null;
        RequestExchange params = RequestExchange.parseRequest(exchange);

        if (params.params("username").length() > 0 && params.params("password").length() > 0)
        {
            String tokenUsername = params.params("username");
            String tokenPassword = params.params("password");
            if (checkCredentials(tokenUsername, tokenPassword))
            {
                authenticate = new Authenticator.Success(new HttpPrincipal(tokenUsername, realm));
            }
            else
            {
                authenticate = new Authenticator.Failure(401);
            }
        }
        else
        {
            authenticate = super.authenticate(exchange);
        }

        return authenticate;
    }
}
