/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import com.core.behavior.dto.DayStatusDTO;
import com.core.behavior.util.Constantes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@Entity
@Table(schema = "behavior", name = "week_quality")
public class WeekQuality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_agency", nullable = false, insertable = true, updatable = true)
    private Agency agency;

    @OneToOne
    @JoinColumn(name = "id_calendar", nullable = false, insertable = true, updatable = true)
    private Calendar calendar;

    @Column(name = "week_of_year")
    private Long weekOfYear;

    @Column(name = "monday")
    private String monday;

    @Column(name = "tuesday")
    private String tuesday;

    @Column(name = "wednesday")
    private String wednesday;

    @Column(name = "thursday")
    private String thursday;

    @Column(name = "friday")
    private String friday;

    @Column(name = "saturday")
    private String saturday;

    @Column(name = "sunday")
    private String sunday;

    @Column(name = "delivery_days")
    private Long deliveryDays;

    @Column(name = "year")
    private Long year;

    @JsonIgnore
    @Transient
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @JsonIgnore
    @Transient
    private Map<LocalDate, Double> mapQualities;

    public WeekQuality() {

    }

    public WeekQuality(Agency agency, Calendar calendar, Long weekYear, Long year) {
        this.agency = agency;
        this.calendar = calendar;
        this.weekOfYear = weekYear;
        this.year = year;
    }

    public void setDayQuality(LocalDate date, DayStatusDTO status) {

        DayOfWeek day = date.getDayOfWeek();
        this.increment(day, status);

        switch (day) {
            case MONDAY:
                this.monday = getFormatedValue(date, status);
                ;
                break;
            case TUESDAY:
                this.tuesday = getFormatedValue(date, status);
                ;
                break;
            case WEDNESDAY:
                this.wednesday = getFormatedValue(date, status);
                ;
                break;
            case THURSDAY:
                this.thursday = getFormatedValue(date, status);
                ;
                break;
            case FRIDAY:
                this.friday = getFormatedValue(date, status);
                break;
            case SATURDAY:
                this.saturday = getFormatedValue(date, status);
                break;
            case SUNDAY:
                this.sunday = getFormatedValue(date, status);
                break;
        }
    }

    private Long getValue(String value) {

        if (Optional.ofNullable(value).isPresent()) {
            String v = value.split("-")[1].replaceAll("(\\[|\\])", "");
            return Long.valueOf(v);
        } else {
            return 0L;
        }

    }

    private void increment(DayOfWeek day, DayStatusDTO status) {

        if (status.getQuality() > 0) {
            if (Optional.ofNullable(this.deliveryDays).isPresent()) {
                if (!this.hasValue(day)) {
                    this.deliveryDays = this.deliveryDays.longValue() <= 4 ? this.deliveryDays.longValue() + 1 : this.deliveryDays.longValue();
                }
            } else {
                this.deliveryDays = 1L;
            }
        }
    }

    private boolean hasValue(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return this.getValue(this.monday) > 0;
            case TUESDAY:
                return this.getValue(this.tuesday) > 0;
            case WEDNESDAY:
                return this.getValue(this.wednesday) > 0;
            case THURSDAY:
                return this.getValue(this.thursday) > 0;
            case FRIDAY:
                return this.getValue(this.friday) > 0;
            case SATURDAY:
                return this.getValue(this.saturday) > 0;
            case SUNDAY:
                return this.getValue(this.sunday) > 0;
            default:
                return false;
        }
    }

    private int getWeekOfYear(LocalDate date) {
        WeekFields weekFields = WeekFields.of(new Locale("pt", "br"));
        return date.get(weekFields.weekOfWeekBasedYear());
    }

    private boolean isFirstWeekOfCalendar() {
        int week = this.getWeekOfYear(this.calendar.getDateInit());
        return week == this.weekOfYear.intValue();
    }

    private boolean isLastWeekOfCalendar() {
        int week = this.getWeekOfYear(this.calendar.getDateEnd());
        return week == this.weekOfYear.intValue();
    }

    private String getFormatedValue(LocalDate date, DayStatusDTO status) {
        String da = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return MessageFormat.format("[{0}]-[{1}]", da, status.getQuality().toString());
    }

    private LocalDate getLocalDate(DayOfWeek dayOfWeek) {

        return LocalDate.now()
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, this.weekOfYear)
                .with(TemporalAdjusters.previousOrSame(dayOfWeek));
    }

    private LocalDate getLocalDate(String value) {
        return LocalDate.parse(value, this.formatter);
    }

    private void parseValue(String st) {

        if (Optional.ofNullable(st).isPresent()) {
            String[] valueString = st.split("\\]-\\[");
            LocalDate date = this.getLocalDate(valueString[0].replaceAll("(\\[|\\])", ""));
            Double value = Double.parseDouble(valueString[1].replaceAll("(\\[|\\])", ""));
            this.mapQualities.put(date, value);
        }

    }

    private void prepareData() {

        mapQualities = new LinkedHashMap<>();
        this.parseValue(this.monday);
        this.parseValue(this.tuesday);
        this.parseValue(this.wednesday);
        this.parseValue(this.thursday);
        this.parseValue(this.friday);
        this.parseValue(this.saturday);
        this.parseValue(this.sunday);
    }

    @JsonIgnore
    public Double getScoreQualityDaily() {
        int size = this.mapQualities.size();
        Double avgWeek = this.mapQualities.values().stream().reduce(Double::sum).orElse(0.00D);

        return avgWeek / size;

    }

    public boolean fulfillQualityDaily() {

        this.prepareData();

        boolean result = false;

        switch (this.agency.getFlagMonthly()) {

            case Constantes.AGENCY_FREQUENCY_DAILY:

                int size = this.mapQualities.size();
                Double avgWeek = this.mapQualities.values().stream().reduce(Double::sum).orElse(0.00D);

                result = (avgWeek / size) >= Constantes.PERCENT_QUALITY;

                break;
            case Constantes.AGENCY_FREQUENCY_WEEKLY:

                if (isFirstWeekOfCalendar()) {

                    List<DayOfWeek> days = this.mapQualities.keySet().stream().map(LocalDate::getDayOfWeek).filter((d) -> d.compareTo(DayOfWeek.WEDNESDAY) < 0).collect(Collectors.toList());

                    if (days.isEmpty()) {
                        result = true;//Deveria ser False ??????
                    } else {
                        LocalDate localMonday = this.getLocalDate(DayOfWeek.MONDAY);
                        LocalDate localTuesday = this.getLocalDate(DayOfWeek.TUESDAY);

                        //Trata feriados
                        Double valueMonday = this.mapQualities.containsKey(localMonday) ? this.mapQualities.get(localMonday) : 0.0D;
                        Double valueTuesday = this.mapQualities.containsKey(localTuesday) ? this.mapQualities.get(localTuesday) : 0.0D;

                        result = Math.max(valueMonday, valueTuesday) >= Constantes.PERCENT_QUALITY;
                    }

                } else if (isLastWeekOfCalendar()) {

                    List<DayOfWeek> days = this.mapQualities.keySet().stream().map(LocalDate::getDayOfWeek).filter((d) -> d.compareTo(DayOfWeek.WEDNESDAY) < 0).collect(Collectors.toList());

                    if (!days.isEmpty()) {
                        LocalDate localMonday = this.getLocalDate(DayOfWeek.MONDAY);
                        LocalDate localTuesday = this.getLocalDate(DayOfWeek.TUESDAY);

                        //Trata feriados
                        Double valueMonday = this.mapQualities.containsKey(localMonday) ? this.mapQualities.get(localMonday) : 0.0D;
                        Double valueTuesday = this.mapQualities.containsKey(localTuesday) ? this.mapQualities.get(localTuesday) : 0.0D;

                        result = Math.max(valueMonday, valueTuesday) >= Constantes.PERCENT_QUALITY;
                    } else {
                        result = true;
                    }

                } else {
                    LocalDate localMonday = this.getLocalDate(DayOfWeek.MONDAY);
                    LocalDate localTuesday = this.getLocalDate(DayOfWeek.TUESDAY);

                    //Trata feriados
                    Double valueMonday = this.mapQualities.containsKey(localMonday) ? this.mapQualities.get(localMonday) : 00.D;
                    Double valueTuesday = this.mapQualities.containsKey(localTuesday) ? this.mapQualities.get(localTuesday) : 00.D;

                    result = Math.max(valueMonday, valueTuesday) >= Constantes.PERCENT_QUALITY;
                }

                break;
        }

        return result;

    }

    public double fullQualityScore() {

        return 0.0;

    }

}
