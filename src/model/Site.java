package model;

public class Site {
    private int id;
    private String name;

    public Site() {
    }

    public Site(String name) {
        this.name = name;
    }

    public Site(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Site(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Site)) return false;

        Site site = (Site) o;

        if (id != site.id) return false;
        return name != null ? name.equals(site.name) : site.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
