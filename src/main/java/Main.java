import org.javatuples.Pair;
import org.joml.Math;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    public static void main(String[] args) {
//        System.setErr(Utils.printStreamErr);
        new Main().run();
    }

    private long windowHandle = NULL;

    void run() {

        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);

        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        try {
            init();
            loop();
        } finally {
            // Release window and window callbacks
            glfwFreeCallbacks(windowHandle);
            glfwDestroyWindow(windowHandle);
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();

//            System.out.println(Utils.stdErrBytes.toString(StandardCharsets.UTF_8));

            glfwSetErrorCallback(null).free();
            GL.setCapabilities(null);
        }
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Illegal state exception");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

        int width = 800;
        int height = 600;

        windowHandle = glfwCreateWindow(width, height, "LearnOpenGL", NULL, NULL);

        if (windowHandle == NULL) {
            System.out.println("Failed to create GLFW window");
            glfwTerminate();
        }

        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        // Get the resolution of the primary monitor
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window

        glfwSetWindowPos(
                windowHandle,
                (videoMode.width() - width) / 2,
                (videoMode.height() - height) / 2
        );

        glfwMakeContextCurrent(windowHandle);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowHandle);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    private void loop() {

        ShaderProgram shaderProgram = new ShaderProgram()
                .createVertexShader(Utils.readFileAsString(Utils.SHADERS_BASE_PATH + "/vertex.glsl"))
                .createFragmentShader(Utils.readFileAsString(Utils.SHADERS_BASE_PATH + "/fragment.glsl"))
                .link()
                .bind();


        Pair<Integer, Integer> vaoAndTexture = Examples.loadTexture();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(windowHandle)) {
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glBindTexture(GL_TEXTURE_2D, vaoAndTexture.getValue1());
            glBindVertexArray(vaoAndTexture.getValue0());
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwSwapBuffers(windowHandle); // swap the color buffers
            glfwPollEvents();
        }
    }




}
