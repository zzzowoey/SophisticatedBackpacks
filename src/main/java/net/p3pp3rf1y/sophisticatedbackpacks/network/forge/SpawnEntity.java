package net.p3pp3rf1y.sophisticatedbackpacks.network.forge;

import io.github.fabricators_of_create.porting_lib.util.LogicalSidedProvider;
import net.fabricmc.api.EnvType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import java.util.Optional;
import java.util.UUID;

public class SpawnEntity extends SimplePacketBase
{
    private final Entity entity;
    private final int typeId;
    private final int entityId;
    private final UUID uuid;
    private final double posX, posY, posZ;
    private final byte pitch, yaw, headYaw;
    private final int velX, velY, velZ;

    public SpawnEntity(Entity e)
    {
        this.entity = e;
        this.typeId = BuiltInRegistries.ENTITY_TYPE.getId(e.getType()); //TODO: Codecs
        this.entityId = e.getId();
        this.uuid = e.getUUID();
        this.posX = e.getX();
        this.posY = e.getY();
        this.posZ = e.getZ();
        this.pitch = (byte) Mth.floor(e.getXRot() * 256.0F / 360.0F);
        this.yaw = (byte) Mth.floor(e.getYRot() * 256.0F / 360.0F);
        this.headYaw = (byte) (e.getYHeadRot() * 256.0F / 360.0F);
        Vec3 vec3d = e.getDeltaMovement();
        double d1 = Mth.clamp(vec3d.x, -3.9D, 3.9D);
        double d2 = Mth.clamp(vec3d.y, -3.9D, 3.9D);
        double d3 = Mth.clamp(vec3d.z, -3.9D, 3.9D);
        this.velX = (int) (d1 * 8000.0D);
        this.velY = (int) (d2 * 8000.0D);
        this.velZ = (int) (d3 * 8000.0D);
    }

    private SpawnEntity(int typeId, int entityId, UUID uuid, double posX, double posY, double posZ, byte pitch, byte yaw, byte headYaw, int velX, int velY, int velZ)
    {
        this.entity = null;
        this.typeId = typeId;
        this.entityId = entityId;
        this.uuid = uuid;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.pitch = pitch;
        this.yaw = yaw;
        this.headYaw = headYaw;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
    }

    public SpawnEntity(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readInt(), new UUID(buf.readLong(), buf.readLong()), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readByte(), buf.readByte(), buf.readByte(), buf.readShort(), buf.readShort(), buf.readShort());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(typeId);
        buffer.writeInt(entityId);
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
        buffer.writeDouble(posX);
        buffer.writeDouble(posY);
        buffer.writeDouble(posZ);
        buffer.writeByte(pitch);
        buffer.writeByte(yaw);
        buffer.writeByte(headYaw);
        buffer.writeShort(velX);
        buffer.writeShort(velY);
        buffer.writeShort(velZ);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.byId(typeId);
            Optional<Level> world = LogicalSidedProvider.CLIENTWORLD.get(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT ? EnvType.CLIENT : EnvType.SERVER);
            Entity e = world.map(type::create).orElse(null);
            if (e == null)
            {
                return;
            }

            /*
             * Sets the postiion on the client, Mirrors what
             * Entity#recreateFromPacket and LivingEntity#recreateFromPacket does.
             */
            e.syncPacketPositionCodec(posX, posY, posZ);
            e.absMoveTo(posX, posY, posZ, (yaw * 360) / 256.0F, (pitch * 360) / 256.0F);
            e.setYHeadRot((headYaw * 360) / 256.0F);
            e.setYBodyRot((headYaw * 360) / 256.0F);

            e.setId(entityId);
            e.setUUID(uuid);
            world.filter(ClientLevel.class::isInstance).ifPresent(w -> ((ClientLevel) w).putNonPlayerEntity(entityId, e));
            e.lerpMotion(velX / 8000.0, velY / 8000.0, velZ / 8000.0);
        });
        return true;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public int getTypeId()
    {
        return typeId;
    }

    public int getEntityId()
    {
        return entityId;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public double getPosX()
    {
        return posX;
    }

    public double getPosY()
    {
        return posY;
    }

    public double getPosZ()
    {
        return posZ;
    }

    public byte getPitch()
    {
        return pitch;
    }

    public byte getYaw()
    {
        return yaw;
    }

    public byte getHeadYaw()
    {
        return headYaw;
    }

    public int getVelX()
    {
        return velX;
    }

    public int getVelY()
    {
        return velY;
    }

    public int getVelZ()
    {
        return velZ;
    }
}

