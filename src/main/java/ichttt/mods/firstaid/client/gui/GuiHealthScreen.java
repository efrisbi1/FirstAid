package ichttt.mods.firstaid.client.gui;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.client.ClientProxy;
import ichttt.mods.firstaid.client.util.EventCalendar;
import ichttt.mods.firstaid.client.util.GuiUtils;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import ichttt.mods.firstaid.common.network.MessageApplyHealingItem;
import ichttt.mods.firstaid.common.network.MessageClientUpdate;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiHealthScreen extends GuiScreen {
    public static GuiHealthScreen INSTANCE;
    public static final int xSize = 256;
    public static final int ySize = 137;
    private static final DecimalFormat FORMAT = new DecimalFormat("##.#");
    public static final ItemStack BED_ITEMSTACK = new ItemStack(Items.BED);

    public int guiLeft;
    public int guiTop;

    private GuiButton head, leftArm, leftLeg, leftFoot, body, rightArm, rightLeg, rightFoot;

    private final AbstractPlayerDamageModel damageModel;
    private final float bedScaleFactor = EventCalendar.isGuiFun() ? 2F : 1.25F;
    private EnumHand activeHand;
    private final boolean disableButtons;

    public static boolean isOpen = false;

    public GuiHealthScreen(AbstractPlayerDamageModel damageModel) {
        this.damageModel = damageModel;
        disableButtons = true;
    }

    public GuiHealthScreen(AbstractPlayerDamageModel damageModel, EnumHand activeHand) {
        this.damageModel = damageModel;
        this.activeHand = activeHand;
        disableButtons = false;
    }

    @Override
    public void initGui() {
        isOpen = true;
        this.guiLeft = (this.width - xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;

        head = new GuiButton(1, this.guiLeft + 4, this.guiTop + 8, 52, 20, I18n.format("gui.head"));
        this.buttonList.add(head);

        leftArm = new GuiButton(2, this.guiLeft + 4, this.guiTop + 33, 52, 20, I18n.format("gui.left_arm"));
        this.buttonList.add(leftArm);
        leftLeg = new GuiButton(3, this.guiLeft + 4, this.guiTop + 58, 52, 20, I18n.format("gui.left_leg"));
        this.buttonList.add(leftLeg);
        leftFoot = new GuiButton(4, this.guiLeft + 4, this.guiTop + 83, 52, 20, I18n.format("gui.left_foot"));
        this.buttonList.add(leftFoot);

        body = new GuiButton(5, this.guiLeft + 199, this.guiTop + 8, 52, 20, I18n.format("gui.body"));
        this.buttonList.add(body);

        rightArm = new GuiButton(6, this.guiLeft + 199, this.guiTop + 33, 52, 20, I18n.format("gui.right_arm"));
        this.buttonList.add(rightArm);
        rightLeg = new GuiButton(7, this.guiLeft + 199, this.guiTop + 58, 52, 20, I18n.format("gui.right_leg"));
        this.buttonList.add(rightLeg);
        rightFoot = new GuiButton(8, this.guiLeft + 199, this.guiTop + 83, 52, 20, I18n.format("gui.right_foot"));
        this.buttonList.add(rightFoot);

        if (disableButtons) {
            head.enabled = false;
            leftArm.enabled = false;
            leftLeg.enabled = false;
            leftFoot.enabled = false;
            body.enabled = false;
            rightArm.enabled = false;
            rightLeg.enabled = false;
            rightFoot.enabled = false;
        }

        GuiButton buttonCancel = new GuiButton(9, this.width / 2 - 100, this.height - 50, I18n.format("gui.cancel"));
        this.buttonList.add(buttonCancel);

        if (this.mc.gameSettings.showDebugInfo) {
            GuiButton refresh = new GuiButton(10, this.guiLeft + 218, this.guiTop + 115, 36, 20, "resync");
            this.buttonList.add(refresh);
        }

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //Setup background
        this.drawDefaultBackground();
        this.drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + xSize, this.guiTop + ySize, -16777216, -16777216);
        this.mc.getTextureManager().bindTexture(GuiUtils.GUI_LOCATION);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
        //Player
        int entityLookX = this.guiLeft + (xSize / 2) - mouseX;
        int entityLookY = this.guiTop + 20 - mouseY;
        if (EventCalendar.isGuiFun()) {
            entityLookX = -entityLookX;
            entityLookY = -entityLookY;
        }
        GuiInventory.drawEntityOnScreen(this.width / 2, this.height / 2 + 30, 45, entityLookX, entityLookY, mc.player);

        //Button
        super.drawScreen(mouseX, mouseY, partialTicks);

        //Text info
        int morphineTicks = damageModel.getMorphineTicks();
        if (morphineTicks > 0)
            drawCenteredString(this.mc.fontRenderer, I18n.format("gui.morphine_left", StringUtils.ticksToElapsedTime(morphineTicks)), this.guiLeft + (xSize / 2), this.guiTop + ySize - (this.activeHand == null ? 21 : 29), 0xFFFFFF);
        if (this.activeHand != null)
            drawCenteredString(this.mc.fontRenderer, I18n.format("gui.apply_hint"), this.guiLeft + (xSize / 2), this.guiTop + ySize - (morphineTicks == 0 ? 21 : 11), 0xFFFFFF);

        //Health
        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        boolean playerDead = damageModel.isDead(mc.player);
        GlStateManager.color(1, 1, 1, 1);
        drawHealth(damageModel.HEAD, false, 14, playerDead);
        drawHealth(damageModel.LEFT_ARM, false, 39, playerDead);
        drawHealth(damageModel.LEFT_LEG, false, 64, playerDead);
        drawHealth(damageModel.LEFT_FOOT, false, 89, playerDead);
        drawHealth(damageModel.BODY, true, 14, playerDead);
        drawHealth(damageModel.RIGHT_ARM, true, 39, playerDead);
        drawHealth(damageModel.RIGHT_LEG, true, 64, playerDead);
        drawHealth(damageModel.RIGHT_FOOT, true, 89, playerDead);

        //Tooltip
        GlStateManager.pushMatrix();
        tooltipButton(head, damageModel.HEAD, mouseX, mouseY);
        tooltipButton(leftArm, damageModel.LEFT_ARM, mouseX, mouseY);
        tooltipButton(leftLeg, damageModel.LEFT_LEG, mouseX, mouseY);
        tooltipButton(leftFoot, damageModel.LEFT_FOOT, mouseX, mouseY);
        tooltipButton(body, damageModel.BODY, mouseX, mouseY);
        tooltipButton(rightArm, damageModel.RIGHT_ARM, mouseX, mouseY);
        tooltipButton(rightLeg, damageModel.RIGHT_LEG, mouseX, mouseY);
        tooltipButton(rightFoot, damageModel.RIGHT_FOOT, mouseX, mouseY);
        GlStateManager.popMatrix();

        //Sleep info setup
        float sleepHealing = FirstAid.activeHealingConfig.sleepHealing;
        int renderBedX = Math.round(guiLeft / bedScaleFactor) + 2;
        int renderBedY = Math.round((guiTop + ySize) / bedScaleFactor) - 18;
        int bedX = (int) (renderBedX * bedScaleFactor);
        int bedY = (int) (renderBedY * bedScaleFactor);

        //Sleep info icon
        GlStateManager.pushMatrix();
        if (sleepHealing > 0F) RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.scale(bedScaleFactor, bedScaleFactor, bedScaleFactor);
        mc.getRenderItem().renderItemAndEffectIntoGUI(null, BED_ITEMSTACK, renderBedX, renderBedY);
        GlStateManager.popMatrix();

        //Sleep info tooltip
        if (mouseX >= bedX && mouseY >= bedY && mouseX < bedX + (16 * bedScaleFactor) && mouseY < bedY + (16 * bedScaleFactor)) {
            String s = sleepHealing == 0F ? I18n.format("gui.no_sleep_heal") : I18n.format("gui.sleep_heal_amount", FORMAT.format(sleepHealing / 2));
            drawHoveringText(s, mouseX, mouseY);
        }

        //TODO color the critical parts of the player red?
    }

    private void tooltipButton(GuiButton button, AbstractDamageablePart part, int mouseX, int mouseY) {
        boolean enabled = part.activeHealer == null;
        if (!enabled && button.hovered)
            drawHoveringText(I18n.format("gui.active_item") + ": " + I18n.format(part.activeHealer.stack.getUnlocalizedName() + ".name"), mouseX, mouseY);
        if (!disableButtons)
            button.enabled = enabled;
    }

    public void drawHealth(AbstractDamageablePart damageablePart, boolean right, int yOffset, boolean playerDead) {
        int xTranslation = guiLeft + (right ? 200 - Math.min(38, GuiUtils.getMaxHearts(damageablePart.getMaxHealth()) * 9 + GuiUtils.getMaxHearts(damageablePart.getAbsorption()) * 9 + 2) : 57);
        GuiUtils.drawHealth(damageablePart, xTranslation, guiTop + yOffset, this, true, playerDead);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == ClientProxy.showWounds.getKeyCode())
            mc.displayGuiScreen(null);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id < 9) {
            EnumPlayerPart playerPart = EnumPlayerPart.fromID((button.id));
            FirstAid.NETWORKING.sendToServer(new MessageApplyHealingItem(playerPart, activeHand));
            //TODO notify the user somehow (sound?)
            AbstractDamageablePart part = damageModel.getFromEnum(playerPart);
            part.activeHealer = FirstAidRegistryImpl.INSTANCE.getPartHealer(mc.player.getHeldItem(this.activeHand));
        } else if (button.id == 10) {
            FirstAid.NETWORKING.sendToServer(new MessageClientUpdate(MessageClientUpdate.Type.REQUEST_REFRESH));
            FirstAid.logger.info("Requesting refresh");
            mc.player.sendStatusMessage(new TextComponentString("Re-downloading health data from server..."), true);
        }
        mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        INSTANCE = null;
        isOpen = false;
        super.onGuiClosed();
    }

    public List<GuiButton> getButtons() {
        return buttonList;
    }
}