package io.github.techstreet.dfscript.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.techstreet.dfscript.DFScript;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vector4f;
import org.lwjgl.opengl.GL11;

public class RenderUtil {

    private static final List<Scissor> scissorStack = new ArrayList<>();

    public static void renderImage(MatrixStack stack, int x, int y, int width, int height, float ux, float uy, float uw, float uh, String image) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, new Identifier(image));
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableCull();

        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder bb = tessellator.getBuffer();
        bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bb.vertex(stack.peek().getPositionMatrix(), x, y, 0).texture(ux, uy).next();
        bb.vertex(stack.peek().getPositionMatrix(), x + width, y, 0).texture(ux+uw, uy).next();
        bb.vertex(stack.peek().getPositionMatrix(), x + width, y + height, 0).texture(ux+uw, uy+uh).next();
        bb.vertex(stack.peek().getPositionMatrix(), x, y + height, 0).texture(ux, uy+uh).next();
        tessellator.draw();
    }

    public static void renderButton(MatrixStack stack, int x, int y, int width, int height, boolean hovered, boolean disabled) {
        final String image = "textures/gui/widgets.png";
        final int textureWidth = 256;
        final int textureHeight = 256;
        final int padding = 3;
        int x1 = 0;
        int y1 = 66;
        if (disabled) {
            y1 = 46;
        } else if (hovered) {
            y1 = 86;
        }
        int x2 = 200;
        int y2 = y1 + 20;
        renderContinuousTexture(stack, x, y, width, height, image, x1, y1, x2, y2, textureWidth, textureHeight, padding,1);
    }

    public static void renderCustomButton(MatrixStack stack, int x, int y, int width, int height, boolean hovered, boolean disabled, String image) {
        final int textureWidth = 256;
        final int textureHeight = 256;
        final int padding = 3;
        int x1 = 0;
        int y1 = 66;
        if (disabled) {
            y1 = 46;
        } else if (hovered) {
            y1 = 86;
        }
        int x2 = 200;
        int y2 = y1 + 20;
        renderContinuousTexture(stack, x, y, width, height, image, x1, y1, x2, y2, textureWidth, textureHeight, padding,1);
    }

    public static void renderContinuousTexture(MatrixStack stack, int x, int y, int width, int height, String image, int tx1, int ty1, int tx2, int ty2, int textureWidth, int textureHeight, int padding,double scale) {
        int scaledPadding = (int) (padding*scale);
        //top left corner
        renderContinuousTexture(stack, x, y, scaledPadding, scaledPadding, image, tx1, ty1, tx1 + padding, ty1 + padding, textureWidth, textureHeight,scale);
        //top right corner
        renderContinuousTexture(stack, x + width - scaledPadding, y, scaledPadding, scaledPadding, image, tx2 - padding, ty1, tx2, ty1 + padding, textureWidth, textureHeight,scale);
        //bottom left corner
        renderContinuousTexture(stack, x, y + height - scaledPadding, scaledPadding, scaledPadding, image, tx1, ty2 - padding, tx1 + padding, ty2, textureWidth, textureHeight,scale);
        //bottom right corner
        renderContinuousTexture(stack, x + width - scaledPadding, y + height - scaledPadding, scaledPadding, scaledPadding, image, tx2 - padding, ty2 - padding, tx2, ty2, textureWidth, textureHeight,scale);

        //top
        renderContinuousTexture(stack, x + scaledPadding, y, width - scaledPadding * 2, scaledPadding, image, tx1 + padding, ty1, tx2 - padding, ty1 + padding, textureWidth, textureHeight,scale);
        //bottom
        renderContinuousTexture(stack, x + scaledPadding, y + height - scaledPadding, width - scaledPadding * 2, scaledPadding, image, tx1 + padding, ty2 - padding, tx2 - padding, ty2, textureWidth, textureHeight,scale);
        //left
        renderContinuousTexture(stack, x, y + scaledPadding, scaledPadding, height - scaledPadding * 2, image, tx1, ty1 + scaledPadding, tx1 + padding, ty2 - padding, textureWidth, textureHeight,scale);
        //right
        renderContinuousTexture(stack, x + width - scaledPadding, y + scaledPadding, scaledPadding, height - scaledPadding * 2, image, tx2 - padding, ty1 + padding, tx2, ty2 - padding, textureWidth, textureHeight,scale);

        //center
        renderContinuousTexture(stack, x + scaledPadding, y + scaledPadding, width - scaledPadding * 2, height - scaledPadding * 2, image, tx1 + padding, ty1 + padding, tx2 - padding, ty2 - padding, textureWidth, textureHeight,scale);
    }

    public static void renderContinuousTexture(MatrixStack stack, int x, int y, int width, int height, String image, int tx1, int ty1, int tx2, int ty2, int textureWidth, int textureHeight, double scale) {
        int tw = (tx2 - tx1);
        int th = (ty2 - ty1);

        float ux = (float) tx1 / textureWidth;
        float uy = (float) ty1 / textureHeight;
        float uw = (float) tw / textureWidth;
        float uh = (float) th / textureHeight;

        tw*=scale;
        th*=scale;

        float xrepeations = (float) width / (tw);
        float yrepeations = (float) height / (th);
        for (int xi = 0; xi <= xrepeations - 1; xi++) {
            for (int yi = 0; yi <= yrepeations - 1; yi++) {
                renderImage(stack,x + xi * tw, y + yi * th, tw, th, ux, uy, uw, uh, image);
            }
        }
        float minH = Math.min(th, yrepeations * th + 1);

        for (int xi = 0; xi < xrepeations - 1; xi++) {
            renderImage(stack, x + xi * tw, Math.max((int) (y + yrepeations * th) - th, y), tw, (int) minH, ux, uy, uw, uh, image);
        }
        float minW = Math.min(tw, xrepeations * tw + 1);
        for (int yi = 0; yi < yrepeations - 1; yi++) {
            renderImage(stack, Math.max((int) (x + xrepeations * tw) - tw, x), y + yi * th, (int) minW, th, ux, uy, uw, uh, image);
        }
        renderImage(stack, Math.max((int) (x + xrepeations * tw) - tw, x), Math.max((int) (y + yrepeations * th) - th, y), (int) minW, (int) minH, ux, uy, uw, uh, image);
    }

    public static void renderGui(MatrixStack stack, int x, int y, int width, int height) {
        renderContinuousTexture(stack, x, y, width, height, "textures/gui/demo_background.png", 0, 0, 248, 166, 256, 256, 10,0.66);
    }

    public static void pushScissor(int x, int y, int width, int height) {
        if (scissorStack.size() != 0) {
            Scissor state = scissorStack.get(scissorStack.size() - 1);
            x = Math.max(x, state.x);
            y = Math.max(y, state.y);
            width = Math.min(width, state.x + state.width - x);
            height = Math.min(height, state.y + state.height - y);
        }

        Scissor s = new Scissor(x, y, width, height);
        scissorStack.add(s);
        GL11.glScissor(x, DFScript.MC.getWindow().getHeight() - y - height, width, height);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void popScissor() {
        scissorStack.remove(scissorStack.size() - 1);
        if (scissorStack.size() > 0) {
            Scissor s = scissorStack.get(scissorStack.size() - 1);
            GL11.glScissor(s.x, DFScript.MC.getWindow().getHeight() - s.y - s.height, s.width, s.height);
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public static void renderGuiItem(MatrixStack stack, ItemStack item) {
        stack.push();
        ItemRenderer renderer = DFScript.MC.getItemRenderer();
        Vector4f pos = new Vector4f(0, 0, 0, 1);
        pos.transform(stack.peek().getPositionMatrix());
        renderer.renderGuiItemIcon(item, (int) pos.getX(), (int) pos.getY());
        stack.pop();
    }

    private record Scissor(int x, int y, int width, int height) {

    }

    public static void sendToaster(String title, String description, SystemToast.Type type) {
        sendToaster((MutableText) Text.of(title), (MutableText) Text.of(description), type);
    }

    public static void sendToaster(MutableText title, MutableText description, SystemToast.Type type) {
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(type, title, description));
    }
}