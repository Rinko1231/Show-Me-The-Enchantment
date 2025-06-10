package com.rinko1231.showmetheenchantment.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends AbstractContainerScreen<EnchantmentMenu>  {

    public EnchantmentScreenMixin(AbstractContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super((EnchantmentMenu) pMenu, pPlayerInventory, pTitle);
    }

    @Unique
    int Rinko1231$x;
    @Unique
    int Rinko1231$y;

    @Shadow
    @Final
    private RandomSource random;
    @Unique
    EnchantmentScreen Rinko1231$enchantmentScreen = (EnchantmentScreen) (Object) (this);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private boolean showMeTheEnchantment(List<MutableComponent> instance, Object e) {
        for (int i = 0; i < 3; i++) {
            if (this.isHovering(60, 14 + 19 * i, 108, 17, Rinko1231$x, Rinko1231$y)) {
                List<EnchantmentInstance> list;
                list = Rinko1231$getEnchantmentList(Rinko1231$enchantmentScreen.getMenu().getSlot(0).getItem(), i, Rinko1231$enchantmentScreen.getMenu().costs[i]);
                for (EnchantmentInstance enchantmentInstance : list) {
                    instance.add( Enchantment.getFullname(enchantmentInstance.enchantment,enchantmentInstance.level).copy().withStyle(ChatFormatting.GRAY));
                }
            }
        }
        return true;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/EnchantmentScreen;isHovering(IIIIDD)Z"))
    private void isHoveringGetter(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        Rinko1231$x = pMouseX;
        Rinko1231$y = pMouseY;
    }

    @Unique
    private List<EnchantmentInstance> Rinko1231$getEnchantmentList(ItemStack p_39472_, int p_39473_, int p_39474_) {
        this.random.setSeed(Rinko1231$enchantmentScreen.getMenu().getEnchantmentSeed() + p_39473_);
        Level level = Minecraft.getInstance().level;
        assert level != null;
        Optional<HolderSet.Named<Enchantment>> optional =  level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
        List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, p_39472_, p_39474_, optional.get().stream());
        if (p_39472_.is(Items.BOOK) && list.size() > 1) {
            list.remove(this.random.nextInt(list.size()));
        }
        return list;
    }


}