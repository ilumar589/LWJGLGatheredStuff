import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class ShaderProgram {

    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public ShaderProgram() {
        programId = glCreateProgram();
    }

    public ShaderProgram createVertexShader(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);

        return this;
    }

    public ShaderProgram createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);

        return this;
    }

    public ShaderProgram link() {
        glLinkProgram(programId);
        logProgramStatus(programId, GL_LINK_STATUS);
        if (vertexShaderId != NULL) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != NULL) {
            glDetachShader(programId, fragmentShaderId);
        }
        glValidateProgram(programId);
        logProgramStatus(programId, GL_VALIDATE_STATUS);

        return this;
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != NULL) {
            glDeleteProgram(programId);
        }
    }

    private int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);
        logShaderStatus(shaderId, GL_COMPILE_STATUS);
        glAttachShader(programId, shaderId);

        return shaderId;
    }

    private void logShaderStatus(int shaderId, int statusToCheck) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            if (glGetShaderi(shaderId, statusToCheck) == NULL) {
                ByteBuffer infoLog = stack.malloc(256 * Integer.BYTES);
                glGetShaderInfoLog(shaderId, new int[256], infoLog);

                System.out.println(infoLog.asReadOnlyBuffer().toString());
            }
        }
    }

    private void logProgramStatus(int shaderId, int statusToCheck) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            if (glGetProgrami(shaderId, statusToCheck) == NULL) {
                ByteBuffer infoLog = stack.malloc(256 * Integer.BYTES);
                glGetShaderInfoLog(shaderId, new int[256], infoLog);

                System.out.println(infoLog.asReadOnlyBuffer().toString());
            }
        }
    }
}
