/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.service.push.business;

import java.util.List;

import com.assembly.service.push.domain.PushPacket;

/**
 *
 * @author administrator
 */
public interface PushInterface
{
    public List<String> tokens(String username);

    public boolean push(PushPacket pushPacket);
}
