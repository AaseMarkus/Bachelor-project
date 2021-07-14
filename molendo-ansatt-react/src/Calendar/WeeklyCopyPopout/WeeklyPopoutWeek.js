import React, { Component } from 'react'
import i18n from './../../Language/i18n'
import './WeeklyPopoutWeek.css'

export default class WeeklyPopoutWeek extends Component {
    constructor(props) {
        super(props)
    
        this.state = {
             checked: false,
        }

        this.changeChecked = this.changeChecked.bind(this)
    }

    changeChecked() {
        this.setState(prevState => {
            return {
                checked: !prevState.checked
            }
        }, () => {
            this.props.changeCopiedWeek(this.state.checked, this.props.weekNumber)
        })
    }
    

    render() {
        return (
            <div
                className="weekContainerPopout"
                onClick={this.changeChecked}>
                <input
                    className="week-inp-cbx"
                    type="checkbox"
                    checked={this.state.checked}
                    readOnly
                />
                <span>
                    {i18n.t('week.week')} {this.props.week}
                </span>     
            </div>
        )
    }
}
