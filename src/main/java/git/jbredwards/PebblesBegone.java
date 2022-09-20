package git.jbredwards;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.Name("Pebbles Begone Plugin")
@Mod(modid = "pebbles_begone", name = "Pebbles Begone", version = "1.0", acceptedMinecraftVersions = "[1.8.9,1.12.2]")
public final class PebblesBegone implements IFMLLoadingPlugin
{
    static boolean obfuscated = true;
    public static final class Transformer implements IClassTransformer, Opcodes
    {
        @Override
        public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
            if("vazkii.botania.common.world.SkyblockWorldEvents".equals(transformedName)) {
                final ClassNode classNode = new ClassNode();
                new ClassReader(basicClass).accept(classNode, 0);
                //does the changes
                methods:
                for(MethodNode method : classNode.methods) {
                    if(method.name.equals("onPlayerInteract")) {
                        /*
                         * Old code:
                         * Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
                         *
                         * New code:
                         * Block block = null;
                         */
                        for(AbstractInsnNode insn : method.instructions.toArray()) {
                            if(insn instanceof MethodInsnNode && ((MethodInsnNode)insn).name.equals(obfuscated ? "func_177230_c" : "getBlock")) {
                                method.instructions.insert(insn, new InsnNode(ACONST_NULL));
                                method.instructions.remove(insn.getPrevious());
                                method.instructions.remove(insn.getPrevious());
                                method.instructions.remove(insn.getPrevious());
                                method.instructions.remove(insn.getPrevious());
                                method.instructions.remove(insn.getPrevious());
                                method.instructions.remove(insn);
                                break methods;
                            }
                        }
                    }
                }
                //writes the changes
                final ClassWriter writer = new ClassWriter(0);
                classNode.accept(writer);
                //returns the transformed class
                return writer.toByteArray();
            }

            return basicClass;
        }
    }

    @Nonnull
    @Override
    public String[] getASMTransformerClass() { return new String[] {"git.jbredwards.PebblesBegone$Transformer"}; }

    @Override
    public void injectData(@Nonnull Map<String, Object> data) {
        obfuscated = (Boolean)data.get("runtimeDeobfuscationEnabled");
    }

    @Nullable
    @Override
    public String getModContainerClass() { return null; }

    @Nullable
    @Override
    public String getSetupClass() { return null; }

    @Nullable
    @Override
    public String getAccessTransformerClass() { return null; }
}
