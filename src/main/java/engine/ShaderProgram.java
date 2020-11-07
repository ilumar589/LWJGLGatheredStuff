package engine;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL20.*;
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
        logStatus(programId, GL_LINK_STATUS);
        if (vertexShaderId != NULL) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != NULL) {
            glDetachShader(programId, fragmentShaderId);
        }
        glValidateProgram(programId);
        logStatus(programId, GL_VALIDATE_STATUS);

        return this;
    }

    public ShaderProgram bind() {
        glUseProgram(programId);

        return this;
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

    public int getProgramId() {
        return programId;
    }

    private int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);
        logStatus(shaderId, GL_COMPILE_STATUS);
        glAttachShader(programId, shaderId);

        return shaderId;
    }

    private void logStatus(int id, int statusToCheck) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer infoLog = stack.malloc(256 * Integer.BYTES);
            int[] infoLogLength = new int[256];

            switch (statusToCheck) {
                case GL_COMPILE_STATUS:
                    if (glGetShaderi(id, statusToCheck) == NULL) {
                        glGetShaderInfoLog(id, infoLogLength, infoLog);
                    }break;
                case GL_LINK_STATUS:
                case GL_VALIDATE_STATUS:
                    if (glGetProgrami(id, statusToCheck) == NULL) {
                        glGetProgramInfoLog(id, infoLogLength, infoLog);
                    }break;
                default:
                    throw new RuntimeException("No recognized log action has been sent: " + statusToCheck);
            }

            System.out.println(infoLog.asReadOnlyBuffer().toString());
        }
    }
}
