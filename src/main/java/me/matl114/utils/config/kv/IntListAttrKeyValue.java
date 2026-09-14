package me.matl114.utils.config.kv;

import java.util.ArrayList;
import java.util.List;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.WrapperFactory;

public class IntListAttrKeyValue extends ListAttrKeyValue<Integer> {
    public static final AttrKeyValue.CustomWidgetFactory<List<Integer>> LIST_WIDGET_FACTORY =
            (s, x, y, inputDx, dy) -> new KalamaHelperHelperCX(x, y, inputDx, dy)
                    .Q(McWidgetHelpers.createAttrValueEditBox(s, 0, 0, inputDx - dy, dy))
                    .Q(WidgetUtils.createOpenListModifyScreenButton(
                            (IntListAttrKeyValue) s, inputDx - dy + 1, 0, dy - 1, dy));

    public IntListAttrKeyValue(String key, List<Integer> value, WrapperFactory<String, List<Integer>> wrapperFactory) {
        super(key, value, LIST_WIDGET_FACTORY, wrapperFactory);
    }

    @Override
    public List<AttrKeyValue<Integer>> createAttrKeyValueForElements() {
        List<Integer> list = (List<Integer>) (Object) this.getOriginValue();
        int size = list.size();
        List<AttrKeyValue<Integer>> res = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            BaseAttrKeyValue<Integer> str = AttrKeyValue.integer(this.getKeyName(), list.get(i));
            str.getValidators().addAll(this.elementValidators);
            res.add(str);
        }

        return res;
    }

    @Override
    public AttrKeyValue<Integer> createNewAttrKeyValueElement() {
        BaseAttrKeyValue<Integer> str = AttrKeyValue.integer(this.getKeyName(), 0);
        str.getValidators().addAll(this.elementValidators);
        return str;
    }
}
