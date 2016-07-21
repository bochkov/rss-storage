package com.sergeybochkov.rss.radioutkin.service;

import com.sergeybochkov.rss.radioutkin.dao.QaDao;
import com.sergeybochkov.rss.radioutkin.domain.Qa;
import com.sergeybochkov.rss.radioutkin.service.worker.ProSportOnlineWorker;
import com.sergeybochkov.rss.radioutkin.service.worker.RadioUtkinWorker;
import com.sergeybochkov.rss.radioutkin.service.worker.SovSportWorker;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class QaServiceImpl implements QaService {

    private static final Logger LOG = Logger.getLogger(QaServiceImpl.class.getName());

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    private final QaDao qaDao;

    @Autowired
    public QaServiceImpl(QaDao qaDao) {
        this.qaDao = qaDao;
    }

    @Override
    public void add(Qa qa) {
        qaDao.save(qa);
    }

    @Override
    public Qa get(String id) {
        return qaDao.get(id);
    }

    @Override
    public List<Qa> getLatest(int limit) {
        return qaDao.getLatest(limit);
    }

    @Override
    public boolean find(Qa qa) {
        return qaDao.find(qa);
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    public void downloadSovSport() {
        LOG.info("Starting SovSportWorker");
        executor.submit(new SovSportWorker(this));
    }

    @Transactional
    //@Scheduled(cron="0 */15 * * * ?")
    public void downloadProSport() {
        LOG.info("Starting ProSportOnlineWorker");
        executor.submit(new ProSportOnlineWorker(this));
    }

    @Transactional
    //@Scheduled(cron="0 */15 * * * ?")
    public void downloadRadioUtkin() {
        LOG.info("Starting RadioUtkinWorker");
        executor.submit(new RadioUtkinWorker(this));
    }

    //@Scheduled(cron = "0 */30 * * * ?")
    public void clean() {
        LOG.info("Start cleaning");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -2);
        qaDao.removeOldest(cal.getTime());
    }
}
