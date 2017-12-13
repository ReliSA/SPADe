package com.assembla;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import com.assembla.enums.IntValuedEnum;
import com.assembla.enums.ValuedEnum;
import com.assembla.serialization.NewLineToListDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonRootName(value = "space")
public class Space {

	public enum SpaceStatus implements IntValuedEnum {
		PROPOSED(0), ACTIVE(1), ARCHIVED(2), SHARED(4);

		private int value;

		private SpaceStatus(int value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public Integer getValue() {
			return this.value;
		}

		@JsonCreator
		public static SpaceStatus parse(int value) {
			return ValuedEnum.parse(value, SpaceStatus.values());
		}
	}

	public enum TeamTabRole implements IntValuedEnum {
		ALL(0), WATCHERS_AND_USERS(10), MEMBERS_AND_USERS(50), OWNER(90);

		private int value;

		TeamTabRole(int value) {
			this.value = value;
		}

		@JsonValue
		public Integer getValue() {
			return value;
		}

		@JsonCreator
		public static TeamTabRole parse(int value) {
			return ValuedEnum.parse(value, TeamTabRole.values());
		}

	}

	private SpaceStatus status;
	private Integer bannerHeight;
	private String banner;
	private ZonedDateTime updatedAt;
	private LocalDate lastPayerChangedAt;
	private TeamTabRole teamTabRole;
	private ZonedDateTime createdAt;
	private Boolean approved;
	@JsonDeserialize(using = NewLineToListDeserializer.class)
	private List<String> tabsOrder;
	private Boolean isCommercial;
	private Boolean isManager;
	private TeamPermission teamPermissions;
	private Boolean canJoin;
	private String bannerText;
	private Boolean restricted;
	private Boolean sharePermissions;
	private Boolean canApply;
	private Boolean isVolunteer;
	private String wikiName;
	private String name;
	private String style;
	private String parentId;
	private String defaultShowpage;
	private String description;
	private String id;
	private String bannerLink;
	private ZonedDateTime commercialFrom;
	private LocalDate restrictedDate;
	private WatcherPermission watcherPermissions;
	private PublicPermission publicPermissions;

	public Space() {
	}

	public SpaceStatus getStatus() {
		return status;
	}

	public Space setStatus(SpaceStatus status) {
		this.status = status;
		return this;
	}

	public Integer getBannerHeight() {
		return bannerHeight;
	}

	public Space setBannerHeight(Integer bannerHeight) {
		this.bannerHeight = bannerHeight;
		return this;
	}

	public String getBanner() {
		return banner;
	}

	public Space setBanner(String banner) {
		this.banner = banner;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Space setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public LocalDate getLastPayerChangedAt() {
		return lastPayerChangedAt;
	}

	public Space setLastPayerChangedAt(LocalDate lastPayerChangedAt) {
		this.lastPayerChangedAt = lastPayerChangedAt;
		return this;
	}

	public TeamTabRole getTeamTabRole() {
		return teamTabRole;
	}

	public Space setTeamTabRole(TeamTabRole teamTabRole) {
		this.teamTabRole = teamTabRole;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public Space setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Boolean getApproved() {
		return approved;
	}

	public Space setApproved(Boolean approved) {
		this.approved = approved;
		return this;
	}

	public List<String> getTabsOrder() {
		return tabsOrder;
	}

	public Space setTabsOrder(List<String> tabsOrder) {
		this.tabsOrder = tabsOrder;
		return this;
	}

	public Boolean getIsCommercial() {
		return isCommercial;
	}

	public Space setIsCommercial(Boolean isCommercial) {
		this.isCommercial = isCommercial;
		return this;
	}

	public Boolean getIsManager() {
		return isManager;
	}

	public Space setIsManager(Boolean isManager) {
		this.isManager = isManager;
		return this;
	}

	public TeamPermission getTeamPermissions() {
		return teamPermissions;
	}

	public Space setTeamPermissions(TeamPermission teamPermissions) {
		this.teamPermissions = teamPermissions;
		return this;
	}

	public Boolean getCanJoin() {
		return canJoin;
	}

	public Space setCanJoin(Boolean canJoin) {
		this.canJoin = canJoin;
		return this;
	}

	public String getBannerText() {
		return bannerText;
	}

	public Space setBannerText(String bannerText) {
		this.bannerText = bannerText;
		return this;
	}

	public Boolean getRestricted() {
		return restricted;
	}

	public Space setRestricted(Boolean restricted) {
		this.restricted = restricted;
		return this;
	}

	public Boolean getSharePermissions() {
		return sharePermissions;
	}

	public Space setSharePermissions(Boolean sharePermissions) {
		this.sharePermissions = sharePermissions;
		return this;
	}

	public Boolean getCanApply() {
		return canApply;
	}

	public Space setCanApply(Boolean canApply) {
		this.canApply = canApply;
		return this;
	}

	public Boolean getIsVolunteer() {
		return isVolunteer;
	}

	public Space setIsVolunteer(Boolean isVolunteer) {
		this.isVolunteer = isVolunteer;
		return this;
	}

	public String getWikiName() {
		return wikiName;
	}

	public Space setWikiName(String wikiName) {
		this.wikiName = wikiName;
		return this;
	}

	public String getName() {
		return name;
	}

	public Space setName(String name) {
		this.name = name;
		return this;
	}

	public String getStyle() {
		return style;
	}

	public Space setStyle(String style) {
		this.style = style;
		return this;
	}

	public String getParentId() {
		return parentId;
	}

	public Space setParentId(String parentId) {
		this.parentId = parentId;
		return this;
	}

	public String getDefaultShowpage() {
		return defaultShowpage;
	}

	public Space setDefaultShowpage(String defaultShowpage) {
		this.defaultShowpage = defaultShowpage;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Space setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getId() {
		return id;
	}

	public Space setId(String id) {
		this.id = id;
		return this;
	}

	public String getBannerLink() {
		return bannerLink;
	}

	public Space setBannerLink(String bannerLink) {
		this.bannerLink = bannerLink;
		return this;
	}

	public ZonedDateTime getCommercialFrom() {
		return commercialFrom;
	}

	public Space setCommercialFrom(ZonedDateTime commercialFrom) {
		this.commercialFrom = commercialFrom;
		return this;
	}

	public LocalDate getRestrictedDate() {
		return restrictedDate;
	}

	public Space setRestrictedDate(LocalDate restrictedDate) {
		this.restrictedDate = restrictedDate;
		return this;
	}

	public WatcherPermission getWatcherPermissions() {
		return watcherPermissions;
	}

	public Space setWatcherPermissions(WatcherPermission watcherPermissions) {
		this.watcherPermissions = watcherPermissions;
		return this;
	}

	public PublicPermission getPublicPermissions() {
		return publicPermissions;
	}

	public void setPublicPermissions(PublicPermission publicPermissions) {
		this.publicPermissions = publicPermissions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Space [");
		if (status != null) {
			builder.append("status=");
			builder.append(status);
			builder.append(", ");
		}
		if (wikiName != null) {
			builder.append("wikiName=");
			builder.append(wikiName);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (description != null) {
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
		}
		builder.append("]");
		return builder.toString();
	}

}
