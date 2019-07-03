/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.activiti.specifications;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellAddress;

/**
 *
 * @author thiag
 */
public class Comments implements Comment{

    private boolean visible;
    private CellAddress address;
    private String author;
    private int row;
    private RichTextString richTextString;
    
    
    @Override
    public void setVisible(boolean bln) {
        this.visible = bln;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public CellAddress getAddress() {
        return address;
    }

    @Override
    public void setAddress(CellAddress ca) {
       
    }

    @Override
    public void setAddress(int i, int i1) {
       
    }
    
    public void setAddress(String addres) {
       this.address = new CellAddress(addres);
    }

    @Override
    public int getRow() {
        return this.row;
    }

    @Override
    public void setRow(int i) {
        this.row = i;
    }

    @Override
    public int getColumn() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setColumn(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    } 
    

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String string) {
     this.author = string;
    }

    @Override
    public RichTextString getString() {
        return richTextString;
    }

    @Override
    public void setString(RichTextString rts) {
        this.richTextString = rts;
    }

    @Override
    public ClientAnchor getClientAnchor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
