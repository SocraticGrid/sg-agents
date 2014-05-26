/*******************************************************************************
 *
 * Copyright (C) 2012 by Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy of 
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ******************************************************************************/
 
 /******************************************************************************
 * Socratic Grid contains components to which third party terms apply. To comply 
 * with these terms, the following notice is provided:
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect. All 
 * rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of the NHIN Connect Project nor the names of its 
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * END OF TERMS AND CONDITIONS
 *
 ******************************************************************************/
package planner.ptsd;

import planner.ptsd.domain.*;
import planner.ptsd.domain.actors.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class PTSDProblemFactory {

    static Random rand;

    static int NUMP = 10;

    static int NUMT = 4;
    static int NUMX = 4;
    static int NUMS = 4;

    static int weeks = 52;


    public static PTSDRoster newProblem( Random rand ) {
        PTSDProblemFactory.rand = rand;

        PTSDRoster problem = new PTSDRoster();

        problem.setId( "PTSD - Plan" );

        problem.setPatients( createPatients() );
        problem.setProviders( createProviders() );
        problem.setTemporalHorizon( createWeekSpan() );

        //TODO Hack...
        Visit.temporalHorizon = problem.getTemporalHorizon();

        problem.setVisits( calculateVisits( problem ) );

        return problem;
    }

    private static List<VisitDate> createWeekSpan() {
        List<VisitDate> dates = new ArrayList<VisitDate>();
        for ( int w = 1; w <= weeks; w++ ) {
            for ( int d = Calendar.MONDAY; d <= Calendar.FRIDAY; d++ ) {
                VisitDate dateAM = new VisitDate( w, d, true, DayOfWeek.valueOfCalendar( d ).name(), DayOfWeek.valueOfCalendar( d ) );
                dates.add( dateAM );
                VisitDate datePM = new VisitDate( w, d, false, DayOfWeek.valueOfCalendar( d ).name(), DayOfWeek.valueOfCalendar( d ) );
                dates.add( datePM );
            }
        }
        return dates;
    }

    private static List<Visit> calculateVisits( PTSDRoster problem ) {
        List<Patient> patients = problem.getPatients();
        List<Visit> visits = new ArrayList<Visit>();
        Visit v;

        for ( Patient p : patients ) {
            switch ( p.getSeverity() ) {
                case MILD:
                    for ( int j = 1; j <= 12; j++ ) {
                        visits.add( new Visit( p, j, p.getOnActiveCareFromWeek() + mildVisitSchedule(j), VisitType.THERAPY ) );
                    }
                    for ( int j = 1; j <= 4; j++ ) {
                        visits.add( new Visit( p, j, -1, VisitType.COUNSELING ) );
                    }
                    if ( p.isOnMedication() ) {
                        for ( int j = 1; j <= 4; j++ ) {
                            visits.add( new Visit( p, j, -1, VisitType.MEDICATION ) );
                        }
                    }
                    break;
                case MEDIUM:
                    for ( int j = 1; j <= 20; j++ ) {
                        visits.add( new Visit( p, j, p.getOnActiveCareFromWeek() + mediumVisitSchedule(j), VisitType.THERAPY ) );
                    }
                    for ( int j = 1; j <= 6; j++ ) {
                        visits.add( new Visit( p, j, -1, VisitType.COUNSELING ) );
                    }
                    if ( p.isOnMedication() ) {
                        for ( int j = 1; j <= 6; j++ ) {
                            visits.add( new Visit( p, j, -1, VisitType.MEDICATION ) );
                        }
                    }
                    break;
                case SEVERE:
                    for ( int j = 1; j <= 30; j++ ) {
                        visits.add( new Visit( p, j, p.getOnActiveCareFromWeek() + severeVisitSchedule(j), VisitType.THERAPY ) );
                    }
                    for ( int j = 1; j <= 7; j++ ) {
                        visits.add( new Visit( p, j, -1, VisitType.COUNSELING ) );
                    }
                    if ( p.isOnMedication() ) {
                        for ( int j = 1; j <= 7; j++ ) {
                            visits.add( new Visit( p, j, -1, VisitType.MEDICATION ) );
                        }
                    }
                    break;
            }
            if ( p.isOnGroup() ) {
                for ( int j = 1; j <= 6; j++ ) {
                    visits.add( new Visit( p, j, -1, VisitType.GROUP ) );
                }
            }

        }

        return visits;
    }

    private static int mildVisitSchedule( int j ) {
        return ( ( j <= 10 ) ? j : 10 + 2*( j - 10 ) ) - 1;
    }

    private static int mediumVisitSchedule( int j ) {
        return ( ( j <= 15 ) ? j : 15 + 2*( j - 15 ) ) - 1;
    }

    private static int severeVisitSchedule( int j ) {
        if ( j <= 20 ) {
            return ( j - 1 ) / 2;
        } else if ( j <= 25 ) {
            return ( 10 + ( j - 20 ) ) - 1;
        } else if ( j <= 28 ) {
            return ( 15 + 2*( j - 25 ) ) - 1;
        } else {
            return ( 21 + 4*( j - 28 ) ) - 1;
        }
    }


    private static List<Provider> createProviders( ) {
        List<Provider> providers = new ArrayList<Provider>();

        for ( int j = 0; j < NUMT; j++ ) {
            providers.add( new Therapist( "T" + j, "Dr. " + j ) );
        }

        for ( int j = 0; j < NUMX; j++ ) {
            providers.add( new Psychiatrist( "X" + j, "Dr. " + j + ", MD" ) );
        }

        for ( int j = 0; j < NUMS; j++ ) {
            providers.add( new SocialWorker( "S" + j, "Mr. " + j ) );
        }

        return providers;
    }

    private static List<Patient> createPatients() {
        List<Patient> patients = new ArrayList<Patient>();

        for ( int j = 0; j < NUMP; j++ ) {
            Patient p = new Patient( "" + j, "patient " + j, randomSeverity(), 1 );
            patients.add( p );
        }

        return patients;
    }



    public static boolean onGroup( Severity severity ) {
        double odds = rand.nextDouble();
        switch ( severity ) {
            case NONE   : return false;
            case MILD   : return odds < 0.5;
            case MEDIUM : return odds < 0.5;
            case SEVERE : return odds < 0.5;
            default     : return  false;
        }
    }

    public static boolean onMedication( Severity severity ) {
        double odds = rand.nextDouble();
        switch ( severity ) {
            case NONE   : return false;
            case MILD   : return odds < 0.2;
            case MEDIUM : return odds < 0.3;
            case SEVERE : return odds < 0.4;
            default     : return false;
        }
    }

    public static Severity randomSeverity() {
        double odds = rand.nextDouble();
        if ( odds < 0.5 ) {
            return Severity.MILD;
        } else if ( odds < 0.8 ) {
            return Severity.MEDIUM;
        } else {
            return Severity.SEVERE;
        }
    }
}
