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
		public final static int close = 268435456;
		public final static int list = 268435457;
		public final static int edit = 268435458;
		public final static int progress = 268435459;
		public final static int timer = 268435460;
		public final static int message = 268435461;
		public final static int proposal = 268435462;
		public final static int propose = 268435463;
		public final static int kill = 268435464;
		public final static int pardon = 268435465;
	}

	public final static class string {
		public final static ValueLink close = new ValueLink(new EnvValue[] { new EnvValue(null, null, null, null, "close") });
		public final static ValueLink you_defeated = new ValueLink(new EnvValue[] { new EnvValue(null, null, null, null, "You've defeated %s.") });
	}

	public final static class layout {
		public final static ResourceLink minedroid_test = new ResourceLink(MODID, "layouts", "minedroid_test.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink attacker_vassal_proposal = new ResourceLink(MODID, "layouts", "attacker_vassal_proposal.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink become_vassal = new ResourceLink(MODID, "layouts", "become_vassal.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink wait_for_conqueror = new ResourceLink(MODID, "layouts", "wait_for_conqueror.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink guild = new ResourceLink(MODID, "layouts", "guild.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
	}

	public final static class drawable {
		public final static ResourceLink black = new ResourceLink(MODID, "drawables", "black.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink scroll = new ResourceLink(MODID, "drawables", "scroll.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink bg_overlay = new ResourceLink(MODID, "drawables", "bg_overlay.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink fg_progress_view = new ResourceLink(MODID, "drawables", "fg_progress_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink bg_edit_text = new ResourceLink(MODID, "drawables", "bg_edit_text.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink bg_button = new ResourceLink(MODID, "drawables", "bg_button.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink bg_progress_view = new ResourceLink(MODID, "drawables", "bg_progress_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink button_red = new ResourceLink(MODID, "drawables", "button_red.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink panel = new ResourceLink(MODID, "drawables", "panel.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
		public final static ResourceLink noitems = new ResourceLink(MODID, "drawables", "noitems.xml", new EnvParams[] { new EnvParams(null, null, null, null)});
	}

	public final static class texture {
		public final static ResourceLink test = new ResourceLink(MODID, "textures", "test.png", new EnvParams[] { new EnvParams(null, null, null, null)});
	}

	public final static class ninepatch {
		public final static ResourceLink panel = new ResourceLink(MODID, "ninepatches", "panel", new EnvParams[] { new EnvParams(null, null, null, null)});
	}

	public final static class style {
		public final static Style content_view = new Style(new ResourceLink(MODID, "styles", "content_view.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/view");
		public final static Style relative_layout = new Style(new ResourceLink(MODID, "styles", "relative_layout.xml", new EnvParams[] { new EnvParams(null, null, null, null)}), R.class, "@minedroid:style/view_group");
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