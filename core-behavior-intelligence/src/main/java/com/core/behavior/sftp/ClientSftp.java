/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.sftp;

import com.core.behavior.properties.ClientSftpProperties;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class ClientSftp {

    
    private ChannelSftp channel;

    @Autowired
    private ClientSftpProperties properties;

    public void uploadFile(File file, String folder) throws FileNotFoundException, SftpException, JSchException {
        String path = MessageFormat.format("{0}/{1}/{2}", properties.getBasePath(), folder, file.getName());
        channel = this.channel();
        channel.put(new FileInputStream(file), path);
        channel.exit();
        channel.getSession().disconnect();
    }

    private ChannelSftp channel() throws JSchException {

        Session session = new JSch().getSession(properties.getUser(), properties.getHost(), properties.getPort());
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(properties.getPassword());
        
        session.connect();

        Channel channel = session.openChannel(properties.getProtocol());
        channel.connect();
        return (ChannelSftp) channel;

    }

}
