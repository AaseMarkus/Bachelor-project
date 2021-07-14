import React, { Component } from 'react'
import i18n from './../../Language/i18n'
import WeeklyPopoutWeek from './WeeklyPopoutWeek'

export default class WeeklyCopyPopout extends Component {
    constructor(props) {
        super(props)
    
        this.state = {
             copiedWeeks: [],
        }

        this.changeCopiedWeek = this.changeCopiedWeek.bind(this)
        this.saveCopies = this.saveCopies.bind(this)
    }
    
    changeCopiedWeek(shouldAdd, weekNumber) {
        if (shouldAdd) {
            this.setState(prevState => {
                let newWeeks = prevState.copiedWeeks
                newWeeks.push(weekNumber)

                return {
                    copiedWeeks: newWeeks
                }
            })
        } else {
            this.setState(prevState => {
                let newWeeks = prevState.copiedWeeks.filter(
                    value => value != weekNumber)

                return {
                    copiedWeeks: newWeeks
                }
            })
        }
    }

    saveCopies() {
        this.state.copiedWeeks.forEach(week => {
            let date = new Date(this.props.minDate)
            date = date.setDate(date.getDate() + (week * 7))
            this.props.replaceRostersOnWeek(date, this.props.rostersToCopy,
                this.props.serverTime)
        })
        this.props.toggleOverlay()
    }

    render() {
        let weekList = []
            for(let i = 0; i < this.props.weekAmount; i++) {
                weekList[i] = (
                    <WeeklyPopoutWeek
                        key={i}
                        week={this.props.minWeek + i}
                        serverTime={this.props.serverTime}
                        minDate={this.props.minDate}
                        changeCopiedWeek={this.changeCopiedWeek} 
                        weekNumber={i}
                    />
                )
            }
        return (
            <div>
                <h3><i>{i18n.t('copy.copy-to')}</i></h3>
                {weekList}
                <button
                    className="actionButton newButton"
                    onClick={() => this.saveCopies()}>
                        {i18n.t('buttons.save')}
                </button>
            </div>
        )
    }
}
