package com.onkiup.minedroid;

import com.onkiup.minedroid.gui.resources.*;
import com.onkiup.minedroid.gui.MineDroid;

/**
 * This class is auto generated.
 * Manually made changes will be discarded.
**/
public final class R {
	final static String MODID = "minedroid";
	public final static class id {
		public final static int message = 268435456;
		public final static int hint = 268435457;
		public final static int test = 268435458;
		public final static int text = 268435459;
		public final static int debug = 268435460;
		public final static int close = 268435461;
		public final static int edit = 268435462;
		public final static int edit_multiline = 268435463;
		public final static int progress = 268435464;
		public final static int list = 268435465;
	}

	public final static class string {
		public final static ValueLink cancel = new ValueLink(new EnvValue[] { new EnvValue(null, null, null, null, "Cancel") });
		public final static ValueLink test_window = new ValueLink(new EnvValue[] { new EnvValue(null, null, null, null, "Minedroid Test") });
		public final static ValueLink test = new ValueLink(new EnvValue[] { new EnvValue(null, null, null, null, "test") });
		public final static ValueLink alert_hint = new ValueLink(new EnvValue[] { new EnvValue(null, null, null, null, "click or press Y to dismiss") });
		public final static ValueLink confirm_hint = new ValueLink(new EnvValue[] { new EnvValue(null, null, null, null, "press Y/N to respond") });
		public final static ValueLink ok = new ValueLink(new EnvValue[] { new EnvValue(null, null, null, null, "Ok") });
		public final static ValueLink close = new ValueLink(new EnvValue[] { new EnvValue(null, null, null, null, "close") });
	}

	public final static class layout {
		public final static ResourceLink minedroid_test = new ResourceLink(MODID, "layouts", "minedroid_test.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink alert = new ResourceLink(MODID, "layouts", "alert.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink holder_string = new ResourceLink(MODID, "layouts", "holder_string.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink config_main = new ResourceLink(MODID, "layouts", "config_main.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink confirm = new ResourceLink(MODID, "layouts", "confirm.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
	}

	public final static class drawable {
		public final static ResourceLink shadow = new ResourceLink(MODID, "drawables", "shadow.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink check = new ResourceLink(MODID, "drawables", "check.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink scroll = new ResourceLink(MODID, "drawables", "scroll.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink bg_overlay = new ResourceLink(MODID, "drawables", "bg_overlay.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink bg_checkbox = new ResourceLink(MODID, "drawables", "bg_checkbox.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink fg_progress_view = new ResourceLink(MODID, "drawables", "fg_progress_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink bg_edit_text = new ResourceLink(MODID, "drawables", "bg_edit_text.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink overlay = new ResourceLink(MODID, "drawables", "overlay.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink bg_button = new ResourceLink(MODID, "drawables", "bg_button.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink bg_progress_view = new ResourceLink(MODID, "drawables", "bg_progress_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
	}

	public final static class ninepatch {
		public final static ResourceLink panel = new ResourceLink(MODID, "ninepatches", "panel", new EnvParams[] { new EnvParams(null, null, null, null)});
	}

	public final static class style {
		public final static Style focus = new Style(new ResourceLink(MODID, "styles", "focus.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/linear_layout");
		public final static Style content_view = new Style(new ResourceLink(MODID, "styles", "content_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/view");
		public final static Style relative_layout = new Style(new ResourceLink(MODID, "styles", "relative_layout.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/view_group");
		public final static Style checkbox = new Style(new ResourceLink(MODID, "styles", "checkbox.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/content_view");
		public final static Style entity_view = new Style(new ResourceLink(MODID, "styles", "entity_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/content_view");
		public final static Style edit_text = new Style(new ResourceLink(MODID, "styles", "edit_text.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/text_view");
		public final static Style view_group = new Style(new ResourceLink(MODID, "styles", "view_group.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/content_view");
		public final static Style scroll_view = new Style(new ResourceLink(MODID, "styles", "scroll_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/content_view");
		public final static Style overlay = new Style(new ResourceLink(MODID, "styles", "overlay.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class);
		public final static Style progress_view = new Style(new ResourceLink(MODID, "styles", "progress_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/content_view");
		public final static Style text = new Style(new ResourceLink(MODID, "styles", "text.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class);
		public final static Style theme = new Style(new ResourceLink(MODID, "styles", "theme.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class);
		public final static Style text_view = new Style(new ResourceLink(MODID, "styles", "text_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/content_view");
		public final static Style list_view = new Style(new ResourceLink(MODID, "styles", "list_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/linear_layout");
		public final static Style button = new Style(new ResourceLink(MODID, "styles", "button.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/text_view");
		public final static Style view = new Style(new ResourceLink(MODID, "styles", "view.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class);
		public final static Style linear_layout = new Style(new ResourceLink(MODID, "styles", "linear_layout.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/view_group");
	}

}