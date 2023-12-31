package com.PVPProtection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.Text;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@Singleton
public class RankedCaseManager
{
	// This list is our live end point to pull ranks from
	private static final HttpUrl PVPProtection_FAST_RANK_LIST = HttpUrl.parse("https://pvpprotection.rs/api/ranks.json");

	// This list is the github-pages deployed copy of the live data, has a slight delay but better than regular github lists
	private static final HttpUrl PVPProtection_DEFAULT_RANK_LIST = HttpUrl.parse("https://pvpprotection.github.io/pvpProtectionLists/ranks.json");

	private static final Type typeToken = new TypeToken<List<RankedPlayer>>()
	{
	}.getType();

	private final OkHttpClient client;
	private final ClientThread clientThread;
	private final Gson gson;

	private final Queue<RankedPlayer> dmCases = new ConcurrentLinkedQueue<>();

	@Getter
	private ConcurrentHashMap<String, String> mappingsRankedRSN = new ConcurrentHashMap<>();

	@Inject
	private RankedCaseManager(OkHttpClient client, ClientThread clientThread, Gson gson)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.gson = gson.newBuilder().create();
	}

	/**
	 * @param onComplete called once the list has been refreshed. Called on the client thread
	 */
	public void refresh(Runnable onComplete, boolean useLiveList)
	{
		Request rwReq;

		if (useLiveList)
		{
			rwReq = new Request.Builder().url(PVPProtection_FAST_RANK_LIST).addHeader("Cache-Control", "no-cache").build();
		}
		else
		{
			rwReq = new Request.Builder().url(PVPProtection_DEFAULT_RANK_LIST).addHeader("Cache-Control", "no-cache").build();
		}
		// call on background thread
		client.newCall(rwReq).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.error("failed to get PVPProtection list: {}", e.toString());
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try
				{
					if (response.code() < 200 || response.code() >= 400)
					{
						log.error("failed to get watchlist. status: {}", response.code());
					}
					else
					{
						List<RankedPlayer> cases = gson.fromJson(new InputStreamReader(response.body().byteStream()), typeToken);
						dmCases.clear();
						ConcurrentHashMap<String, String> localMappingsRanks = new ConcurrentHashMap<>();

						for (RankedPlayer c : cases)
						{
							localMappingsRanks.put(c.getNiceRSN(), c.getRank());
							dmCases.add(c);
						}

						mappingsRankedRSN = localMappingsRanks;
						log.debug("saved {}/{} dm cases", dmCases.size(), cases.size());
					}
				}
				finally
				{
					response.close();
					if (onComplete != null)
					{
						clientThread.invokeLater(onComplete);
					}
				}
			}
		});
	}

	/**
	 * Get a PVPProtection RankedPlayer from the cached list
	 *
	 * @param rsn
	 * @return
	 */
	public RankedPlayer get(String rsn)
	{
		if (rsn == null || dmCases.size() == 0)
		{
			return null;
		}
		String cleanRsn = Text.removeTags(Text.toJagexName(rsn)).toLowerCase();

		if (!mappingsRankedRSN.containsKey(cleanRsn))
		{
			return null;
		}

		Optional<RankedPlayer> foundCase = dmCases.stream().filter(c -> c.getNiceRSN().equals(cleanRsn)).findFirst();

		if (foundCase.isPresent())
		{
			return foundCase.get();
		}
		else
		{
			return null;
		}
	}
}