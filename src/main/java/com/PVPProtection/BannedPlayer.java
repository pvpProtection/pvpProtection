package com.PVPProtection;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.client.util.Text;

@Data
@AllArgsConstructor
public class BannedPlayer
{
	@SerializedName("rsn")
	private String rsn;

	@SerializedName("aid")
	private String accountHash;

	@SerializedName("hid")
	private String hardwareID;

	@SerializedName("uid")
	private String hardwareID2;

	@SerializedName("context")
	private String reason;

	public String getNiceRSN()
	{
		return Text.toJagexName(rsn.toLowerCase());
	}
}