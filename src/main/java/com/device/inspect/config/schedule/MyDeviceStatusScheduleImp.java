package com.device.inspect.config.schedule;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.InspectDataRepository;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 */
@Component
public class MyDeviceStatusScheduleImp implements  MySchedule {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private InspectDataRepository inspectDataRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StoreyRepository storeyRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Scheduled(cron = "* 0/5 * * * ? ")
    @Override
    public void scheduleTask() {
        Iterable<Company> companies = companyRepository.findAll();
        if (null!=companies)
            for (Company company:companies){
                if (null!=company.getBuildings())
                    for (Building building : company.getBuildings()){
                        if (null!=building.getFloorList())
                            for (Storey floor : building.getFloorList()){
                                if (null!=floor.getRoomList())
                                    for (Room room:floor.getRoomList()){
                                        if (null!=room.getDeviceList())
                                            for (Device device :room.getDeviceList()){

                                            }
                                    }
                            }
                    }
            }

    }
}
