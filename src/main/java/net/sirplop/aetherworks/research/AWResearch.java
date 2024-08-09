package net.sirplop.aetherworks.research;

import com.rekindled.embers.item.EmberStorageItem;
import com.rekindled.embers.research.ResearchBase;
import com.rekindled.embers.research.ResearchCategory;
import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.subtypes.ResearchFakePage;
import com.rekindled.embers.research.subtypes.ResearchShowItem;
import com.rekindled.embers.research.subtypes.ResearchSwitchCategory;
import com.rekindled.embers.util.Vec2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.compat.curios.CuriosCompat;

public class AWResearch {
    public static final ResourceLocation PAGE_ICONS = new ResourceLocation(Aetherworks.MODID, "textures/gui/codex_index_icons.png");
    public static final double PAGE_ICON_SIZE = 48;

    public static ResearchCategory categoryAether;
    public static ResearchCategory subCategoryTools;
    public static ResearchBase meteor, gauge, amalgam, moon_harvester, focus_matrix, purify_aetherium, aspectus, forge, heat_dial, heater, cooler, vent, metal_former, anvil, tool_station, pearls, tools;
    public static ResearchBase pobs, pomd, aotr, aosa, sotc, soic, cosb, cosr, crown; //TOOLS
    public static ResearchBase tuning_cylinder, volant_calcifier;
    public static ResearchBase moonsnare_jars, moonsnare_bulb;

    public static void initResearch() {
        categoryAether = new ResearchCategory("aw.aether", PAGE_ICONS, 192, 0);
        Vec2i[] ringPositions = {new Vec2i(1, 1), new Vec2i(0, 3), new Vec2i(0, 5), new Vec2i(1, 7), new Vec2i(11, 7), new Vec2i(12, 5), new Vec2i(12, 3), new Vec2i(11, 1), new Vec2i(4, 1), new Vec2i(2, 4), new Vec2i(4, 7), new Vec2i(8, 7), new Vec2i(10, 4),new Vec2i(8, 1)};
        subCategoryTools = new ResearchCategory("aw.tools", 0).pushGoodLocations(ringPositions);

        meteor = new ResearchBase("aw.meteor", new ItemStack(AWRegistry.AETHER_SHARD.get()), 12, 0);
        meteor.addPage(new ResearchShowItem("aw.meteor_2", ItemStack.EMPTY, 0, 0).addItem(new ResearchShowItem.DisplayItem(new ItemStack(AWRegistry.SUEVITE.get()), new ItemStack(AWRegistry.AETHERIUM_ORE.get()))));
        gauge = new ResearchBase("aw.aetheriometer", new ItemStack(AWRegistry.AETHERIOMETER.get()), 8, 0).addAncestor(meteor);
        amalgam = new ResearchBase("aw.amalgam", new ItemStack(AWRegistry.AETHER_AMALGAM.get()), 11, 3).addAncestor(meteor);
        moon_harvester = new ResearchBase("aw.moon_harvester", new ItemStack(AWRegistry.PRISM.get()), 12, 7).addAncestor(amalgam);
        moon_harvester.addPage(new ResearchShowItem("aw.moon_harvester_2", ItemStack.EMPTY, 0, 0).addItem(new ResearchShowItem.DisplayItem(new ItemStack(AWRegistry.PRISM.get()), new ItemStack(AWRegistry.PRISM_SUPPORT.get()), new ItemStack(AWRegistry.MOONLIGHT_AMPLIFIER.get()))));
        moon_harvester.addPage(new ResearchShowItem("aw.moon_harvester_3", ItemStack.EMPTY, 0, 0).addItem(new ResearchShowItem.DisplayItem(new ItemStack(AWRegistry.PRISM.get()), new ItemStack(AWRegistry.PRISM_SUPPORT.get()), new ItemStack(AWRegistry.MOONLIGHT_AMPLIFIER.get()))));
        focus_matrix = new ResearchBase("aw.focus_matrix", new ItemStack(AWRegistry.CONTROL_MATRIX.get()), 7, 4).addAncestor(moon_harvester);
        purify_aetherium = new ResearchBase("aw.purify_aetherium", new ItemStack(AWRegistry.AETHERIUM_GAS.FLUID_BUCKET.get()), 9, 6).addAncestor(moon_harvester);
        aspectus = new ResearchBase("aw.aspectus", new ItemStack(AWRegistry.AETHER_ASPECTUS.get()), 7, 7).addAncestor(purify_aetherium);

        forge = new ResearchBase("aw.forge", new ItemStack(AWRegistry.FORGE_CORE.get()), 2, 5).addAncestor(purify_aetherium);
        forge.addPage(new ResearchBase("aw.forge_2", ItemStack.EMPTY, 0, 0));
        heat_dial = new ResearchBase("aw.heat_dial", new ItemStack(AWRegistry.HEAT_DIAL.get()), 0, 5).addAncestor(forge);
        heater = new ResearchBase("aw.heater", new ItemStack(AWRegistry.FORGE_HEATER.get()), 4, 7).addAncestor(forge);
        cooler = new ResearchBase("aw.cooler", new ItemStack(AWRegistry.FORGE_COOLER.get()), 2, 7).addAncestor(forge);
        vent = new ResearchBase("aw.vent", new ItemStack(AWRegistry.FORGE_VENT.get()), 0, 7).addAncestor(forge);
        metal_former = new ResearchBase("aw.metal_former", new ItemStack(AWRegistry.FORGE_METAL_FORMER.get()), 4, 3).addAncestor(forge);
        anvil = new ResearchBase("aw.anvil", new ItemStack(AWRegistry.FORGE_ANVIL.get()), 2, 3).addAncestor(forge);
        tool_station = new ResearchBase("aw.tool_station", new ItemStack(AWRegistry.FORGE_TOOL_STATION.get()), 0, 3).addAncestor(forge);

        pearls = new ResearchBase("aw.pearls", new ItemStack(AWRegistry.AETHER_PEARL.get()), 4, 0).addAncestor(metal_former);
        tools = new ResearchBase("aw.tools", new ItemStack(AWRegistry.TOOL_ROD_INFUSED.get()), 0, 0).addAncestor(tool_station).addAncestor(pearls);

        ResearchBase toolsFake = new ResearchFakePage(tools, 6, 4);

        pobs = new ResearchBase("aw.pobs", new ItemStack(AWRegistry.PICKAXE_AETHER.get()), subCategoryTools.popGoodLocation()).addAncestor(toolsFake);
        pomd = new ResearchBase("aw.pomd", new ItemStack(AWRegistry.PICKAXE_EMBER.get()), subCategoryTools.popGoodLocation()).addAncestor(toolsFake);
        aotr = new ResearchBase("aw.aotr", new ItemStack(AWRegistry.AXE_ENDER.get()), subCategoryTools.popGoodLocation()).addAncestor(toolsFake);
        aosa = new ResearchBase("aw.aosa", new ItemStack(AWRegistry.AXE_SCULK.get()), subCategoryTools.popGoodLocation()).addAncestor(toolsFake);
        soic = new ResearchBase("aw.soic", new ItemStack(AWRegistry.SHOVEL_SLIME.get()), subCategoryTools.popGoodLocation()).addAncestor(toolsFake);
        sotc = new ResearchBase("aw.sotc", new ItemStack(AWRegistry.SHOVEL_PRISMARINE.get()), subCategoryTools.popGoodLocation()).addAncestor(toolsFake);
        cosb = new ResearchBase("aw.cosb", new ItemStack(AWRegistry.CROSSBOW_QUARTZ.get()), subCategoryTools.popGoodLocation()).addAncestor(toolsFake);
        cosr = new ResearchBase("aw.cosr", new ItemStack(AWRegistry.CROSSBOW_MAGMA.get()), subCategoryTools.popGoodLocation()).addAncestor(toolsFake);

        crown = new ResearchBase("aw.crown", new ItemStack(AWRegistry.AETHER_CROWN.get()), subCategoryTools.popGoodLocation()).addAncestor(toolsFake);
        crown.addPage(new ResearchShowItem("aw.crown_gem", new ItemStack(AWRegistry.POTION_GEM.get()), 0, 0).addItem(new ResearchShowItem.DisplayItem(new ItemStack(AWRegistry.POTION_GEM.get()))));

        tuning_cylinder = new ResearchBase("aw.tuning_cylinder", new ItemStack(AWRegistry.TUNING_CYLINDER.get()), ResearchManager.subCategoryWeaponAugments.popGoodLocation()).addAncestor(ResearchManager.inferno_forge);
        ResearchManager.subCategoryWeaponAugments.addResearch(tuning_cylinder);
        volant_calcifier = new ResearchBase("aw.volant_calcifier", new ItemStack(AWRegistry.VOLANT_CALCIFIER.get()), ResearchManager.subCategoryProjectileAugments.popGoodLocation()).addAncestor(ResearchManager.inferno_forge);
        ResearchManager.subCategoryProjectileAugments.addResearch(volant_calcifier);

        ItemStack fullJar = EmberStorageItem.withFill(AWRegistry.AETHER_EMBER_JAR.get(), ((EmberStorageItem)AWRegistry.AETHER_EMBER_JAR.get()).getCapacity());
        moonsnare_jars = new ResearchBase("aw.moonsnare_jars", fullJar, 6.5, 7).addAncestor(ResearchManager.jars);
        ResearchManager.categoryMetallurgy.addResearch(moonsnare_jars);

        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.initCuriosCategory();
        }

        subCategoryTools.addResearch(toolsFake);

        subCategoryTools
                .addResearch(pobs)
                .addResearch(pomd)
                .addResearch(aotr)
                .addResearch(aosa)
                .addResearch(soic)
                .addResearch(sotc)
                .addResearch(cosb)
                .addResearch(cosr)
                .addResearch(crown);

        ResearchBase toolsSwitch = makeCategorySwitch(subCategoryTools, 0, 0, new ItemStack(AWRegistry.AETHER_CROWN.get()), 0, 1);
        tools.subCategory = toolsSwitch.addAncestor(tool_station).addAncestor(pearls);

        categoryAether
                .addResearch(meteor)
                .addResearch(gauge)
                .addResearch(pearls)
                .addResearch(amalgam)
                .addResearch(moon_harvester)
                .addResearch(focus_matrix)
                .addResearch(purify_aetherium)
                .addResearch(aspectus)
                .addResearch(forge)
                .addResearch(heat_dial)
                .addResearch(heater)
                .addResearch(cooler)
                .addResearch(vent)
                .addResearch(metal_former)
                .addResearch(anvil)
                .addResearch(tool_station)
                .addResearch(toolsSwitch);

        categoryAether.addPrerequisite(ResearchManager.dawnstone);
        ResearchManager.researches.add(categoryAether);
    }

    private static ResearchSwitchCategory makeCategorySwitch(ResearchCategory targetCategory, int x, int y, ItemStack icon, int u, int v) {
        return (ResearchSwitchCategory) new ResearchSwitchCategory(targetCategory.name+"_category", icon, x, y).setTargetCategory(targetCategory).setIconBackground(ResearchManager.PAGE_ICONS, ResearchManager.PAGE_ICON_SIZE * u, ResearchManager.PAGE_ICON_SIZE * v);
    }
}
