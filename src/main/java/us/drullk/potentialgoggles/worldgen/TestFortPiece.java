package us.drullk.potentialgoggles.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.common.world.PieceBeardifierModifier;
import us.drullk.potentialgoggles.content.GogglesWorldgen;

public class TestFortPiece extends TemplateStructurePiece implements PieceBeardifierModifier {
    public TestFortPiece(StructureTemplateManager templateManager, ResourceLocation location, StructurePlaceSettings settings, BlockPos templatePos) {
        super(GogglesWorldgen.TESTING_FORT.get(), 0, templateManager, location, location.toString(), settings, templatePos);
    }

    public TestFortPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(GogglesWorldgen.TESTING_FORT.get(), tag, context.structureTemplateManager(), rl -> makeSettings(context.structureTemplateManager(), tag, rl));
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString("Rotation", this.placeSettings.getRotation().name());
        tag.putString("Mirror", this.placeSettings.getMirror().name());
    }

    private static StructurePlaceSettings makeSettings(StructureTemplateManager templateManager, CompoundTag tag, ResourceLocation location) {
        StructureTemplate structuretemplate = templateManager.getOrCreate(location);

        Mirror mirror = Mirror.valueOf(tag.getString("Mirror"));
        Rotation rotation = Rotation.valueOf(tag.getString("Rotation"));
        BlockPos blockpos = new BlockPos(structuretemplate.getSize().getX() / 2, 0, structuretemplate.getSize().getZ() / 2);

        return new StructurePlaceSettings().setMirror(mirror).setRotation(rotation).setRotationPivot(blockpos);
    }

    @Override
    protected void handleDataMarker(String pname, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {

    }

    @Override
    public BoundingBox getBeardifierBox() {
        return this.boundingBox;
    }

    @Override
    public TerrainAdjustment getTerrainAdjustment() {
        return TerrainAdjustment.BEARD_BOX;
    }

    @Override
    public int getGroundLevelDelta() {
        return 0;
    }
}
