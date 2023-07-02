/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import io.github.fabricators_of_create.porting_lib.models.obj.UnitTextureAtlasSprite;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Vertex consumer that outputs {@linkplain BakedQuad baked quads}.
 * <p>
 * This consumer accepts data in {@link com.mojang.blaze3d.vertex.DefaultVertexFormat#BLOCK} and is not picky about
 * ordering or missing elements, but will not automatically populate missing data (color will be black, for example).
 */
public class QuadBakingVertexConsumer implements VertexConsumer
{
    private final Map<VertexFormatElement, Integer> ELEMENT_OFFSETS = Util.make(new IdentityHashMap<>(), map -> {
        int i = 0;
        for (var element : DefaultVertexFormat.BLOCK.getElements())
            map.put(element, DefaultVertexFormat.BLOCK.offsets.get(i++) / 4); // Int offset
    });

    private static final int STRIDE = DefaultVertexFormat.BLOCK.getIntegerSize();
    private static final int POSITION = findOffset(DefaultVertexFormat.ELEMENT_POSITION);
    private static final int COLOR = findOffset(DefaultVertexFormat.ELEMENT_COLOR);
    private static final int UV0 = findOffset(DefaultVertexFormat.ELEMENT_UV0);
    private static final int UV1 = findOffset(DefaultVertexFormat.ELEMENT_UV1);
    private static final int UV2 = findOffset(DefaultVertexFormat.ELEMENT_UV2);
    private static final int NORMAL = findOffset(DefaultVertexFormat.ELEMENT_NORMAL);

    private static int findOffset(VertexFormatElement element)
    {
        // Divide by 4 because we want the int offset
        var index = DefaultVertexFormat.BLOCK.getElements().indexOf(element);
        return index < 0 ? -1 : DefaultVertexFormat.BLOCK.offsets.get(index) / 4;
    }

    private static final int QUAD_DATA_SIZE = STRIDE * 4;

    private final Consumer<BakedQuad> quadConsumer;

    int vertexIndex = 0;
    private int[] quadData = new int[QUAD_DATA_SIZE];

    private int tintIndex;
    private Direction direction = Direction.DOWN;
    private TextureAtlasSprite sprite = UnitTextureAtlasSprite.INSTANCE;
    private boolean shade;

    public QuadBakingVertexConsumer(Consumer<BakedQuad> quadConsumer)
    {
        this.quadConsumer = quadConsumer;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z)
    {
        int offset = vertexIndex * STRIDE + POSITION;
        quadData[offset] = Float.floatToRawIntBits((float) x);
        quadData[offset + 1] = Float.floatToRawIntBits((float) y);
        quadData[offset + 2] = Float.floatToRawIntBits((float) z);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        int offset = vertexIndex * STRIDE + NORMAL;
        quadData[offset] = ((int) (x * 127.0f) & 0xFF) |
                (((int) (y * 127.0f) & 0xFF) << 8) |
                (((int) (z * 127.0f) & 0xFF) << 16);
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a)
    {
        int offset = vertexIndex * STRIDE + COLOR;
        quadData[offset] = ((a & 0xFF) << 24) |
                ((b & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (r & 0xFF);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v)
    {
        int offset = vertexIndex * STRIDE + UV0;
        quadData[offset] = Float.floatToRawIntBits(u);
        quadData[offset + 1] = Float.floatToRawIntBits(v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v)
    {
        if (UV1 >= 0) // Vanilla doesn't support this, but it may be added by a 3rd party
        {
            int offset = vertexIndex * STRIDE + UV1;
            quadData[offset] = (u & 0xFFFF) | ((v & 0xFFFF) << 16);
        }
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v)
    {
        int offset = vertexIndex * STRIDE + UV2;
        quadData[offset] = (u & 0xFFFF) | ((v & 0xFFFF) << 16);
        return this;
    }

    @Override
    public void endVertex()
    {
        if (++vertexIndex != 4)
            return;
        // We have a full quad, pass it to the consumer and reset
        quadConsumer.accept(new BakedQuad(quadData, tintIndex, direction, sprite, shade));
        vertexIndex = 0;
        quadData = new int[QUAD_DATA_SIZE];
    }

    @Override
    public void defaultColor(int r, int g, int b, int a)
    {
    }

    @Override
    public void unsetDefaultColor()
    {
    }

    public void setTintIndex(int tintIndex)
    {
        this.tintIndex = tintIndex;
    }

    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    public void setSprite(TextureAtlasSprite sprite)
    {
        this.sprite = sprite;
    }
}