package semverstudy.commons.bcm;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.objectweb.asm.*;
import semverstudy.commons.Logging;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A simple model built from byte code containing basic class and member information.
 * This can be used to look up fully qualified class names in source-code based analyses to avoid resolving imports.
 * @author jens dietrich
 */
public class ByteCodeModel {

    public static final Logger LOGGER = Logging.getLogger("type-lookup");

    private Set<File> jars = null;
    // types by name
    private Map<String,JType> types = null;

    public ByteCodeModel(Set<File> jars) throws IOException {
        this.jars = jars;
        init();
    }

    public ByteCodeModel(File... jars) throws IOException {
        this.jars = Sets.newHashSet(jars);
        init();
    }

    public JType getType(String name) {
        return this.types.get(name);
    }

    public Set<JType> getType(Predicate<String> nameFilter) {
        return this.types.values().parallelStream()
            .filter(type -> nameFilter.test(type.getName()))
            .collect(Collectors.toSet());
    }

    public Collection<JType> getAllTypes() {
        return this.types.values();
    }

    private void init()  {
        LOGGER.info("Indexing bytecode");
        this.types = new HashMap<>();

        LOGGER.info("Indexing bytecode, first pass: index types");
        ClassVisitor typeCollector = new ClassVisitor(Opcodes.ASM7) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(version, access, name, signature, superName, interfaces);
                name = name.replaceAll("/",".");
                types.put(name,new JClass(name));
            }
        };
        for (File jar:this.jars) {
            try {
                analyse(jar, typeCollector);
            }
            catch (IOException x) {
                LOGGER.warn("Error collecting type info from jar file " + jar.getAbsolutePath(),x);
            }
        }

        Function<String,JType> factory = n -> types.computeIfAbsent(n, k -> new JClass(k));

        LOGGER.info("Indexing bytecode, second pass: analysing method and field declarations");
        ClassVisitor methodAnalyser = new ClassVisitor(Opcodes.ASM7) {
            JClass clazz = null;
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(version, access, name, signature, superName, interfaces);
                name = name.replaceAll("/",".");
                clazz = (JClass)types.get(name);
                assert clazz!=null;

            }
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                JType type = DescriptorParser.parseFieldDescriptor(descriptor,factory);
                clazz.fields.add(new JField(name,descriptor,type));
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                Pair<List<JType>, JType> sign = DescriptorParser.parseMethodDescriptor(descriptor,factory);
                clazz.methods.add(new JMethod(name,descriptor,sign.getRight(),sign.getLeft()));
                return null;
            }
        };
        for (File jar:this.jars) {
            try {
                analyse(jar, methodAnalyser);
            }
            catch (IOException x) {
                LOGGER.warn("Error analysing method and fields of some class in jar file " + jar.getAbsolutePath(),x);
            }
        }

        LOGGER.info("Indexing bytecode done");
    }

    private void analyse (File jar, ClassVisitor visitor) throws IOException {
        ZipFile archive = new ZipFile(jar);
        Enumeration<? extends ZipEntry> en = archive.entries();
        while (en.hasMoreElements()) {
            ZipEntry e = en.nextElement();
            String name = e.getName();
            if (name.endsWith(".class")) {
                try (InputStream in = archive.getInputStream(e)) {
                    analyse(in, visitor);
                }
            }
        }
    }

    private void analyse (InputStream in, ClassVisitor visitor) throws IOException {
        new ClassReader(in).accept(visitor, 0);
    }
}
