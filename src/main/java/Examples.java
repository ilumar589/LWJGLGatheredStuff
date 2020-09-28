import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Examples {

    private Examples() {
    }

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

    // EXCERCISE 1 Try to draw 2 triangles next to each other using glDrawArrays by adding more vertices to your data
    public static int exercise1() {
        float[] vertices = {
                -1.0f, 0.0f, 0.0f, // first triangle bottom left
                -0.5f, 1.0f, 0.0f, // first triangle top
                0.0f, 0.0f, 0.0f,  // first triangle bottom right
                0.0f, 0.0f, 0.0f, // second triangle bottom left
                0.5f, 0.5f, 0.0f, // second triangle top
                1.0f, 0.5f, 0.0f  // second triangle bottom right
        };

        int vao, vbo;

        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(vbo, GL_ARRAY_BUFFER);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);

        return vao;
    }
}
