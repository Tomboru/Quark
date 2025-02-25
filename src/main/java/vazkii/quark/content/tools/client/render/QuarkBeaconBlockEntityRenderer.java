package vazkii.quark.content.tools.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import vazkii.quark.content.tools.module.BeaconRedirectionModule;
import vazkii.quark.content.tools.module.BeaconRedirectionModule.ExtendedBeamSegment;

import java.util.List;

// Mostly vanilla copypaste but adapted to use ExtendedBeamSegment values
public class QuarkBeaconBlockEntityRenderer {

	public static boolean render(BeaconBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if (!BeaconRedirectionModule.staticEnabled)
			return false;

		long i = tileEntityIn.getLevel().getGameTime();
		List<BeaconBlockEntity.BeaconBeamSection> list = tileEntityIn.getBeamSections();

		for (BeaconBlockEntity.BeaconBeamSection segment : list) {
			if (!(segment instanceof ExtendedBeamSegment extension))
				return false; // Defer back to the vanilla one

			renderBeamSegment(matrixStackIn, bufferIn, extension, partialTicks, i);
		}

		return true;
	}

	private static void renderBeamSegment(PoseStack matrixStackIn, MultiBufferSource bufferIn, ExtendedBeamSegment segment, float partialTicks, long totalWorldTime) {
		renderBeamSegment(matrixStackIn, bufferIn, BeaconRenderer.BEAM_LOCATION, segment, partialTicks, 1.0F, totalWorldTime, 0.2F, 0.25F);
	}

	public static void renderBeamSegment(PoseStack matrixStackIn, MultiBufferSource bufferIn, ResourceLocation textureLocation, ExtendedBeamSegment segment, float partialTicks, float textureScale, long totalWorldTime, float beamRadius, float glowRadius) {
		int height = segment.getHeight();
		float[] colors = segment.getColor();

		matrixStackIn.pushPose();
		matrixStackIn.translate(0.5D, 0.5D, 0.5D); // Y translation changed to 0.5
		matrixStackIn.translate(segment.offset.getX(), segment.offset.getY(), segment.offset.getZ()); // offset by the correct distance
		matrixStackIn.mulPose(segment.dir.getRotation());

		float angle = -(Math.floorMod(totalWorldTime, 40L) + partialTicks);
		float partAngle = Mth.frac(1 * 0.2F - (float)Mth.floor(angle * 0.1F));
		float r = colors[0];
		float g = colors[1];
		float b = colors[2];

		matrixStackIn.pushPose();
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(angle * 2.25F - 45.0F));
		float v2 = -1.0F + partAngle;
		float v1 = (float)height * textureScale * (0.5F / beamRadius) + v2;
		renderPart(matrixStackIn, bufferIn.getBuffer(RenderType.beaconBeam(textureLocation, false)), r, g, b, 1.0F, height, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius, 0.0F, 1.0F, v1, v2);
		matrixStackIn.popPose();
		v1 = (float)height * textureScale + v2;
		renderPart(matrixStackIn, bufferIn.getBuffer(RenderType.beaconBeam(textureLocation, true)), r, g, b, 0.125F, height, -glowRadius, -glowRadius, glowRadius, -glowRadius, -glowRadius, glowRadius, glowRadius, glowRadius, 0.0F, 1.0F, v1, v2);
		matrixStackIn.popPose();
	}

	private static void renderPart(PoseStack matrixStackIn, VertexConsumer bufferIn, float red, float green, float blue, float alpha, int height, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float u1, float u2, float v1, float v2) {
		PoseStack.Pose pose = matrixStackIn.last();
		Matrix4f matrix4f = pose.pose();
		Matrix3f matrix3f = pose.normal();
		addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, 0, height, x1, y1, x2, y2, u1, u2, v1, v2);
		addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, 0, height, x4, y4, x3, y3, u1, u2, v1, v2);
		addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, 0, height, x2, y2, x4, y4, u1, u2, v1, v2);
		addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, 0, height, x3, y3, x1, y1, u1, u2, v1, v2);
	}

	private static void addQuad(Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer bufferIn, float red, float green, float blue, float alpha, int yMin, int yMax, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
		addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMax, x1, z1, u2, v1);
		addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMin, x1, z1, u2, v2);
		addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMin, x2, z2, u1, v2);
		addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMax, x2, z2, u1, v1);
	}

	private static void addVertex(Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer bufferIn, float red, float green, float blue, float alpha, int y, float x, float z, float texU, float texV) {
		bufferIn.vertex(matrixPos, x, (float)y, z).color(red, green, blue, alpha).uv(texU, texV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrixNormal, 0.0F, 1.0F, 0.0F).endVertex();
	}

}
