/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import java.io.Serializable;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public interface Identifiable<T extends Serializable> {
    T getId();
}
