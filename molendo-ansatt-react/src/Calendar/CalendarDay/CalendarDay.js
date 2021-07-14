import React, { Component } from 'react'
import { Popover, OverlayTrigger } from 'react-bootstrap'
import i18n from './../../Language/i18n'
import RosterTime from './../RosterTime/RosterTime'
import TimePopout from './../TimePopout/TimePopout'
import DailyCopyPopout from './../DailyCopyPopout/DailyCopyPopout'
import LockedIcon from './../../Icons/LockedIcon'
import UnlockedIcon from './../../Icons/UnlockedIcon'
import TrashIcon from './../../Icons/TrashIcon'
import CopyIcon from './../../Icons/CopyIcon'
import PlusIcon from './../../Icons/PlusIcon'
import './CalendarDay.css'

export default class CalendarDay extends Component {
    constructor(props) {
        super(props)
        this.state = {
             locked: false,
             showCopy: false,
             showNew: false
        }
        this.changeLocked = this.changeLocked.bind(this)
        this.toggleCopyPopup = this.toggleCopyPopup.bind(this)
        this.toggleNewPopup = this.toggleNewPopup.bind(this)
    }

    changeLocked() {
        this.setState(prevState => ({
            locked: !prevState.locked
        }))
    }

    toggleCopyPopup() {
        this.setState({showCopy: !this.state.showCopy})
    }

    toggleNewPopup() {
        this.setState({showNew: !this.state.showNew})
    }

    render() {
        let locked = this.state.locked || this.props.locked || this.props.pastDate
        let lockIcon = locked ? <LockedIcon/> : <UnlockedIcon/>
        let date = new Date(this.props.currentDate)
        let finishedRosters = []
        let futureRosters = []

        this.props.dailyPlans.forEach(roster => {
            if (roster.fldOpRLogOn === true && roster.fldOpRLogOut === true) {
                finishedRosters.push(
                    <li key={roster.fldOpRStartTime}>
                        <RosterTime
                            cssTheme={this.props.cssTheme}
                            deleteRoster={this.props.deleteRoster}
                            roster={roster}
                            locked={true} />
                    </li>
                )
            } else {
                futureRosters.push(
                <li key={roster.fldOpRStartTime}>
                    <RosterTime
                        cssTheme={this.props.cssTheme}
                        deleteRoster={this.props.deleteRoster}
                        roster={roster}
                        locked={locked} />
                </li>
                )
            }
        })

        const newPopover = (
            <Popover className={`${this.props.cssTheme} popoverBasic`}>
                <Popover.Content>
                    <TimePopout
                        toggleOverlay={this.toggleNewPopup}
                        addRoster={this.props.addRoster}
                        start={this.props.currentDate} 
                        end={this.props.endDate}
                        date={date.getDate()} 
                        month={i18n.t('months.'+date.getMonth())}
                        dailyPlans={this.props.dailyPlans}
                    />
                </Popover.Content>
            </Popover>
        )
        
        const copyPopover = (
            <Popover className={`${this.props.cssTheme} popoverBasic`}>
                <Popover.Content>
                    <DailyCopyPopout
                        toggleOverlay={this.toggleCopyPopup}
                        minWeek={this.props.minWeek}
                        serverTime={this.props.serverTime}
                        minDate={this.props.minDate}
                        replaceRostersOnDate={this.props.replaceRostersOnDate} 
                        rostersToCopy={this.props.dailyPlans} 
                        weekdays={this.props.weekdays} 
                        weekAmount={this.props.weekAmount} />
                </Popover.Content>
            </Popover>
        )

        const deleteStartTime = this.props.serverTime > this.props.currentDate ? 
            new Date(this.props.serverTime) : new Date(this.props.currentDate)

        return (
            <div className="dayContainer">
                <h3>{this.props.weekday}</h3>
                
                <h4>{`${date.getDate()}. ${i18n.t('months.'+date.getMonth())}`}</h4>
                <ul>
                    <li>
                        <button
                            role="img"
                            title={this.state.locked ? i18n.t('buttons.locked') : i18n.t('buttons.lock')}
                            className="lockButton actionButton"
                            onClick={this.changeLocked}
                            disabled={this.props.locked || this.props.pastDate}>
                            {lockIcon}
                        </button>
                    </li>

                    <li>
                        <button
                            role="img"
                            title={i18n.t('buttons.reset')}
                            className="trashButton actionButton"
                            disabled={locked}
                            onClick={() => this.props.deleteRostersBetweenDates(
                                deleteStartTime, this.props.endDate)}>
                            <TrashIcon/>
                        </button>
                    </li>
                    <li>
                        <OverlayTrigger
                            show={this.state.showCopy}
                            trigger="click"
                            onToggle={this.toggleCopyPopup}
                            rootClose
                            placement="bottom"
                            overlay={copyPopover}>
                            <button
                                role="img"
                                title={i18n.t('buttons.copy')}
                                className="copyButton actionButton"><CopyIcon/>
                            </button>
                        </OverlayTrigger>
                    </li>
                </ul>

                <ul>
                    {finishedRosters}
                    {futureRosters}
                </ul>

                <OverlayTrigger
                    show={this.state.showNew}
                    trigger="click"
                    onToggle={this.toggleNewPopup}
                    placement="bottom" 
                    overlay={newPopover}
                    rootClose>
                    <button 
                        className="newButton actionButton" 
                        disabled={locked}>{locked ? i18n.t('buttons.locked') : i18n.t('day.new')} <PlusIcon/>
                    </button>
                </OverlayTrigger>
            </div>
        )
    }
}
