package semverstudy.meta;

import java.net.URL;
import java.util.Objects;

/**
 * Representation of a Maven-style license.
 * @author jens dietrich
 */
public class License {
    private String name = null;
    private URL url = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        License license = (License) o;
        return Objects.equals(name, license.name) &&
                Objects.equals(url, license.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url);
    }

    @Override
    public String toString() {
        return "License{" +
                "name='" + name + '\'' +
                '}';
    }
}
