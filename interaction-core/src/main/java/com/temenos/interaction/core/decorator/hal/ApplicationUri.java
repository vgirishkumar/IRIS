package com.temenos.interaction.core.decorator.hal;

import java.net.URI;
import java.net.URISyntaxException;

public class ApplicationUri {
    private URI uri;
    
    public ApplicationUri(String uri) {
        try {
            this.uri = new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public ApplicationUri(URI uri) {
        this(uri.toString());
    }
/*
    public RestbucksUri(URI uri, Identifier identifier) {
        this(uri.toString() + "/" + identifier.toString());
    }

    public Identifier getId() {
        String path = uri.getPath();
        return new Identifier(path.substring(path.lastIndexOf("/") + 1, path.length()));
    }
*/
    public URI getFullUri() {
        return uri;
    }
    
    public String toString() {
        return uri.toString();
    }
    
    public boolean equals(Object obj) {
        if(obj instanceof ApplicationUri) {
            return ((ApplicationUri)obj).uri.equals(uri);
        }
        return false;
    }

    public String getBaseUri() {
        String port = "";
        if(uri.getPort() != 80 && uri.getPort() != -1) {
            port = ":" + String.valueOf(uri.getPort());
        }
        
        return uri.getScheme() + "://" + uri.getHost() + port;
    }
}
