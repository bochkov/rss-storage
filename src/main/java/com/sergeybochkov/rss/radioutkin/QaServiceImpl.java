package com.sergeybochkov.rss.radioutkin;

import com.sergeybochkov.rss.radioutkin.source.ProSportOnline;
import com.sergeybochkov.rss.radioutkin.source.RadioUtkin;
import com.sergeybochkov.rss.radioutkin.source.SovSport;
import com.sergeybochkov.rss.store.StoreDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class QaServiceImpl implements QaService {

    private static final Logger LOG = LoggerFactory.getLogger(QaServiceImpl.class);

    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(3);

    private final QaDao qaDao;
    private final StoreDao storeDao;

    @Autowired
    public QaServiceImpl(QaDao qaDao, StoreDao storeDao) {
        this.qaDao = qaDao;
        this.storeDao = storeDao;
    }

    @Override
    public List<Qa> getLatest(int limit) {
        return qaDao.getLatest(limit);
    }

    @Transactional
    //@Scheduled(cron = "0 0 * * * ?")
    public void downloadSovSport() {
        LOG.info("Starting SovSport");
        SERVICE.submit(new SovSport(qaDao, storeDao));
    }

    @Transactional
    //@Scheduled(cron="0 */15 * * * ?")
    public void downloadProSport() {
        LOG.info("Starting ProSportOnline");
        SERVICE.submit(new ProSportOnline(qaDao));
    }

    @Transactional
    //@Scheduled(cron="0 */15 * * * ?")
    public void downloadRadioUtkin() {
        LOG.info("Starting RadioUtkin");
        SERVICE.submit(new RadioUtkin(qaDao));
    }
}
