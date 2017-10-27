package in.ac.amu.zhcet.utils.page;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class PathChain {

    private List<Path> chain = new ArrayList<>();

    // To prevent direct instantiation
    private PathChain() { }

    public static PathChain start() {
        return new PathChain();
    }

    public PathChain add(Path path) {
        chain.add(path);
        return this;
    }

    public PathChain add(Path... paths) {
        chain.addAll(Arrays.asList(paths));
        return this;
    }

    public List<Path> getChain() {
        chain.get(chain.size() - 1).setActive(true);
        return chain;
    }

}
