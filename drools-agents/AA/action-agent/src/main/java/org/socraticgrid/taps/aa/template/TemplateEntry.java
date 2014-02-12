/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.socraticgrid.taps.aa.template;

import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class TemplateEntry implements Serializable{

    private final String id;
    private final String title;
    private final String header;
    private final String body;

    TemplateEntry(String id, String title, String header, String body) {
        this.id = id;
        this.title = title;
        this.header = header;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    
    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }
    
}
