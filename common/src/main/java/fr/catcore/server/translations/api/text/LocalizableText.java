package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public interface LocalizableText extends Component {
    static Component asLocalizedFor(Component text, LocalizationTarget target) {
        if (text instanceof LocalizableText localizableText) {
            return localizableText.asLocalizedFor(target);
        }
        
        return text;
    }

    default Component asLocalizedFor(LocalizationTarget target) {
        LocalizedTextBuilder builder = new LocalizedTextBuilder();
        
        this.visitText(builder, target, Style.EMPTY);
        
        Component result = builder.getResult();
        
        ((LocalizableText) result).setLocalized(true);
        
        return result;
    }

    void visitText(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);

    boolean isLocalized();

    void setLocalized(boolean value);
}
