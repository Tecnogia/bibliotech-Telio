package fr.amu.univ.miage.m1.glq.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utilitaires pour les dates.
 * 
 */
public class DateUtils {
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    public static final long ONE_DAY_MS = 24 * 60 * 60 * 1000;
    public static final long ONE_WEEK_MS = 7 * ONE_DAY_MS;
    
    // Constructeur privé pour classe utilitaire
    private DateUtils() {
    }
    
    /**
     * Formate une date en chaîne.
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return dateFormat.format(date);
    }
    
    /**
     * Formate une date avec l'heure.
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return dateTimeFormat.format(date);
    }
    
    /**
     * Parse une chaîne en date.
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * Calcule le nombre de jours entre deux dates.
     */
    public static int daysBetween(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        long diff = end.getTime() - start.getTime();
        return (int) (diff / ONE_DAY_MS);
    }
    
    /**
     * Ajoute des jours à une date.
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime() + (days * ONE_DAY_MS));
    }
    
    /**
     * Vérifie si une date est dans le passé.
     */
    public static boolean isPast(Date date) {
        if (date == null) {
            return false;
        }
        return date.before(new Date());
    }
    
    /**
     * Vérifie si une date est aujourd'hui.
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * Vérifie si une date est dans les N prochains jours.
     */
    public static boolean isWithinDays(Date date, int days) {
        if (date == null) {
            return false;
        }
        Date now = new Date();
        Date limit = addDays(now, days);
        return date.after(now) && date.before(limit);
    }
    
    /**
     * Retourne le début de la journée (00:00:00).
     */
    public static Date startOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Retourne la fin de la journée (23:59:59).
     */
    public static Date endOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
