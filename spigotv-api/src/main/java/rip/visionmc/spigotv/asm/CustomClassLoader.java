package rip.visionmc.spigotv.asm;

public class CustomClassLoader extends ClassLoader {
    public Class<?> defineClass(String name, byte[] bytecode) {
        return super.defineClass(name, bytecode, 0, bytecode.length);
    }
}
