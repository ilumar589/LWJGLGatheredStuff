package engine;

import org.javatuples.Pair;
import org.joml.Math;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Examples {

    private Examples() {}

    public static int triangleVertexArrayBuffer() {
        float[] triangleData = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f
        };

        int vao, vbo;

        vao = glGenVertexArrays(); // consider a vertex array an object that holds the state of one vertex buffer object,
        // by state meaning all the vertex attribute pointers towards the vertex buffer object data
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, triangleData, GL_STATIC_DRAW);
        glVertexAttribPointer(0,3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0); // we have to enable each attribute pointer

        glBindVertexArray(0);

        return vao;
    }

    public static int squareWithElementBufferObject() {
        // a square is composed of two triangles but seeing as their vertices intersect we
        // can use something called indexed drawing with an element buffer object

        float[] vertices = {
                0.5f,  0.5f, 0.0f,  // top right
                0.5f, -0.5f, 0.0f,  // bottom right
                -0.5f, -0.5f, 0.0f,  // bottom left
                -0.5f,  0.5f, 0.0f   // top left
        };
        int[] indices = {  // note that we start from 0!
                0, 1, 3,   // first triangle
                1, 2, 3    // second triangle
        };

        int vao, vbo, ebo;

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        // ..:: Initialization code :: ..
        // 1. bind Vertex Array Object
        glBindVertexArray(vao);
        // 2. copy our vertices array in a vertex buffer for OpenGL to use
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        // 3. copy our index array in a element buffer for OpenGL to use
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        // 4. then set the vertex attributes pointers
        glVertexAttribPointer(0,3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0); // we have to enable each attribute pointer
        glBindVertexArray(0);

        return vao;
    }

    // Try to draw 2 triangles next to each other using glDrawArrays by adding more vertices to your data
    public static int helloTriangleExercise1() {
        float[] vertices = {
                -1.0f, 0.0f, 0.0f, // first triangle bottom left
                -0.5f, 1.0f, 0.0f, // first triangle top
                0.0f, 0.0f, 0.0f,  // first triangle bottom right

                0.04f, 0.0f, 0.0f, // second triangle bottom left
                0.5f, 1.0f, 0.0f, // second triangle top
                1.0f, 0.0f, 0.0f  // second triangle bottom right
        };

        int vao, vbo;

        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);

        return vao;

//        glDrawArrays(GL_TRIANGLES, 0, 6); used in loop
    }

    // Same two triangles using separate vaos and vbos
    public static Pair<Integer, Integer> helloTriangleExercise2() {
        float[] firstTriangleVertices = {
                -1.0f, 0.0f, 0.0f, // first triangle bottom left
                -0.5f, 1.0f, 0.0f, // first triangle top
                0.0f, 0.0f, 0.0f,  // first triangle bottom right
        };

        int firstTriangleVao, firstTriangleVbo;
        firstTriangleVao = glGenVertexArrays();
        firstTriangleVbo = glGenBuffers();
        glBindVertexArray(firstTriangleVao);
        glBindBuffer(GL_ARRAY_BUFFER, firstTriangleVbo);
        glBufferData(GL_ARRAY_BUFFER, firstTriangleVertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        float[] secondTriangleVertices = {
                0.04f, 0.0f, 0.0f, // second triangle bottom left
                0.5f, 1.0f, 0.0f, // second triangle top
                1.0f, 0.0f, 0.0f  // second triangle bottom right
        };

        int secondTriangleVao, secondTriangleVbo;
        secondTriangleVao = glGenVertexArrays();
        secondTriangleVbo = glGenBuffers();
        glBindVertexArray(secondTriangleVao);
        glBindBuffer(GL_ARRAY_BUFFER, secondTriangleVbo);
        glBufferData(GL_ARRAY_BUFFER, secondTriangleVertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);

        return new Pair<>(firstTriangleVao, secondTriangleVao);
    }

    public static int colorOnAttributes() {
        float[] vertices = {
                //positions          //colors
                0.5f, -0.5f, 0.0f,   1.0f, 0.0f, 0.0f, // bottom right
               -0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f, // bottom left
                0.0f,  0.5f, 0.0f,   0.0f, 0.0f, 1.0f  // top
        };

        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);

        return vao;
    }

    public static Pair<Integer, Integer> loadTexture() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer nrOfChannels = stack.mallocInt(1);


            int texture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture);
            // set the texture wrapping/filtering options (on the currently bound texture object)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            ByteBuffer textureData = stbi_load(Utils.TEXTURES_BASE_PATH + "/container.jpg", width, height, nrOfChannels, 0);
            if (textureData != null && textureData.hasRemaining()) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(), height.get(), 0, GL_RGB, GL_UNSIGNED_BYTE, textureData);
                glGenerateMipmap(GL_TEXTURE_2D);
                stbi_image_free(textureData);
            } else {
                System.out.println("Failed to load texture");
            }


            float[] vertices = {
                    // positions          // colors           // texture coords
                    0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f,   // top right
                    0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f,   // bottom right
                    -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f,   // bottom left
                    -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f    // top left
            };


            int vao = glGenVertexArrays();
            int vbo = glGenBuffers();
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
            glEnableVertexAttribArray(2);

            glBindVertexArray(0);

            return new Pair<>(vao, texture);
        }

    }

    private static void setUniformExample() {
        // update the uniform color
        float timeValue = (float) glfwGetTime();
        float greenValue = Math.sin(timeValue) / 2.0f + 0.5f;
        int vertexColorLocation = glGetUniformLocation(1, "ourColor");
        glUniform4f(vertexColorLocation, 0.0f, greenValue, 0.0f, 1.0f);
    }
}
