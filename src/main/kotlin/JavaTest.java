import io.github.zmilla93.gui.NameTest;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class JavaTest {

    JavaTest() {
        try {
            Files.walkFileTree(Paths.get(""),new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return super.postVisitDirectory(dir, exc);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        NameTest nameTest = new NameTest();
//        System.out.println(nameTest.getS1());
//        System.out.println(nameTest.setS1(""));
    }

}
