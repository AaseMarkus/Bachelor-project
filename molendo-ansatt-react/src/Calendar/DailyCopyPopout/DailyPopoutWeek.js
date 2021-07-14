import React, { Component } from 'react'
import i18n from './../../Language/i18n'
import DailyPopoutDay from './DailyPopoutDay'
import './DailyPopoutWeek.css'

export default class DailyPopoutWeek extends Component {
    render() {
        let dayList = []

        for(let i = 0; i < 7; i++) {
            dayList[i] = (
                <DailyPopoutDay
                    key={i}
                    serverTime={this.props.serverTime}
                    minDate={this.props.minDate}
                    changeCopiedDay={this.props.changeCopiedDay}
                    dayNumber={(this.props.weekNumber * 7) + i}
                    weekday={i18n.t('weekdays.'+i)} 
                />
            )
        }

        return (
            <div>
                <br />
                <h5>{i18n.t('week.week')} {this.props.week}</h5>
                
                <div className="dayListContainer">
                    {dayList}
                </div>
                
            </div>
        )
    }
}
