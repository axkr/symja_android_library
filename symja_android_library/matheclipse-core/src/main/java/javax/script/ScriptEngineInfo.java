package javax.script;


public interface ScriptEngineInfo
{

    public abstract String getEngineName();

    public abstract String getEngineVersion();

    public abstract String[] getExtensions();

    public abstract String[] getMimeTypes();

    public abstract String[] getNames();

    public abstract String getLanguageName();

    public abstract String getLanguageVersion();

    public abstract Object getParameter(String s);

    public abstract String getMethodCallSyntax(String s, String s1, String as[]);

    public abstract String getOutputStatement(String s);

    public abstract String getProgram(String as[]);
}
