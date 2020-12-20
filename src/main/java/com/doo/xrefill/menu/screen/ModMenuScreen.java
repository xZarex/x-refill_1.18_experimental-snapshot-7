package com.doo.xrefill.menu.screen;

import com.doo.xrefill.Refill;
import com.doo.xrefill.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

/**
 * mod menu 配置界面
 */
public class ModMenuScreen extends Screen {

    private static final ModMenuScreen INSTANCE = new ModMenuScreen();
    private static final TranslatableText ENABLE = new TranslatableText("xrefill.menu.option.enable");
    private static final TranslatableText DISABLE = new TranslatableText("xrefill.menu.option.disable");

    private Screen pre;

    private ModMenuScreen() {
        super(new LiteralText(Refill.ID));
        init();
    }

    @Override
    protected void init() {
        // 启用按钮
        this.addButton(new ButtonWidget(this.width / 2 - 150 / 2, 28, 150, 20,
                Refill.option.enable ? ENABLE : DISABLE,
                b -> b.setMessage((Refill.option.clickEnable() ? ENABLE : DISABLE))));
        // 返回按钮
        this.addButton(new ButtonWidget(this.width / 2 - 150 / 2, this.height - 28, 150, 20,
                ScreenTexts.BACK, b -> INSTANCE.close()));
    }

    public static ModMenuScreen get(Screen pre) {
        INSTANCE.width = pre.width;
        INSTANCE.height = pre.height;
        INSTANCE.pre = pre;
        return INSTANCE;
    }

    private void close() {
        if (client != null) {
            // 返回上个页面
            client.currentScreen = this.pre;
            // 保存设置的配置
            Config.write(Refill.ID, Refill.option);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // 画背景
        super.renderBackground(matrices);
        // 画其他
        super.render(matrices, mouseX, mouseY, delta);
    }
}
