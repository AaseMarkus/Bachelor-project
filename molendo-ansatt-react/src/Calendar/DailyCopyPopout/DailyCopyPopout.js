import React, { Component } from 'react'
import i18n from './../../Language/i18n'
import DailyPopoutWeek from './DailyPopoutWeek'

export default class DailyCopyPopout extends Component {
    constructor(props) {
        super(props)
    
        this.state = {
             copiedDays: [],
        }

        this.changeCopiedDay = this.changeCopiedDay.bind(this)
        this.saveCopies = this.saveCopies.bind(this)
    }
    

    changeCopiedDay(shouldAdd, dayNumber) {
        if(shouldAdd) {
            this.setState(prevState => {
                let newDays = prevState.copiedDays
                newDays.push(dayNumber)

                return {
                    copiedDays: newDays
                }
            })
        } else {
            this.setState(prevState => {
                let newDays = prevState.copiedDays.filter(
                    value => value != dayNumber)

                return {
                    copiedDays: newDays
                }
            })
        }
    }

    saveCopies() {
        this.state.copiedDays.forEach(day => {
            let date = new Date(this.props.minDate)
            date = date.setDate(date.getDate() + day)
            this.props.replaceRostersOnDate(date, this.props.rostersToCopy,
               this.props.serverTime)
        })
        this.props.toggleOverlay()
    }

    render() {
        let weekList = []
        for(let i = 0; i < this.props.weekAmount; i++) {
            weekList[i] = (
                <DailyPopoutWeek
                    key={i}
                    week={this.props.minWeek + i}
                    serverTime={this.props.serverTime}
                    minDate={this.props.minDate}
                    changeCopiedDay={this.changeCopiedDay}
                    weekNumber={i}
                    weekdays={this.props.weekdays}
                    />
            )
        }

        return (
            <div>
                <h2><i>{i18n.t('copy.copy-to')}</i></h2>
                {weekList}

                <br />
                <button
                    className="actionButton newButton"
                    type="button"
                    onClick={() => this.saveCopies()}>
                        {i18n.t('buttons.save')}
                </button>
            </div>
        )
    }
}
